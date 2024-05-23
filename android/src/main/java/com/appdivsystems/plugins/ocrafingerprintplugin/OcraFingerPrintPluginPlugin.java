package com.appdivsystems.plugins.ocrafingerprintplugin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.integratedbiometrics.ibscancommon.IBCommon;
import com.integratedbiometrics.ibscanultimate.IBScan;
import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.integratedbiometrics.ibscanultimate.IBScanDeviceListener;
import com.integratedbiometrics.ibscanultimate.IBScanException;
import com.integratedbiometrics.ibscanultimate.IBScanListener;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.integratedbiometrics.ibscanultimate.IBScan.HashType;
@CapacitorPlugin(name = "OcraFingerPrintPlugin")
public class OcraFingerPrintPluginPlugin extends Plugin implements IBScanListener, IBScanDeviceListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private boolean isDeviceInitialized = false;

    private String convertBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private String convertImageDataToBase64(IBScanDevice.ImageData image) {
        if (image == null || image.buffer == null) {
            return null;
        }

        // Assuming image.buffer contains the raw image bytes
        // Determine the actual size of the image data
        int imageSize = image.width * image.height;


        // Ensure the buffer length matches the expected size
        if (image.buffer.length < imageSize) {

            return null;
        }

        // Encode the correct portion of the byte array to Base64 string
//        byte[] imageBytes = new byte[imageSize];
//        System.arraycopy(image.buffer, 0, imageBytes, 0, imageSize);
//
//        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
    Bitmap bitmapData = image.toBitmap();
        return convertBitmapToBase64(bitmapData);
    }

    protected void _Sleep(int time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
        }
    }
    protected final int DEVICE_LOCKED		= 1;
    protected final int DEVICE_UNLOCKED		= 2;
    private IBScanDevice.ImageData m_lastResultImage;
    protected String base64FingerPrintImage = "no base64";

    protected String    m_strCustomerKey = "";
    protected String sampleMessage = "";
    protected String samplePermissionMessage = "";

    protected boolean	m_bInitializing = false;
    protected int m_nDeviceLockState = DEVICE_UNLOCKED;
    protected final int DEVICE_KEY_INVALID	= 4;
    private CountDownLatch latch;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void deviceCommunicationBroken(IBScanDevice device) {

    }

    @Override
    public void deviceImagePreviewAvailable(IBScanDevice device, IBScanDevice.ImageData image) {

    }

    @Override
    public void deviceFingerCountChanged(IBScanDevice device, IBScanDevice.FingerCountState fingerState) {

    }

    @Override
    public void deviceFingerQualityChanged(IBScanDevice device, IBScanDevice.FingerQualityState[] fingerQualities) {

    }

    @Override
    public void deviceAcquisitionBegun(IBScanDevice device, IBScanDevice.ImageType imageType) {

    }

    @Override
    public void deviceAcquisitionCompleted(IBScanDevice device, IBScanDevice.ImageType imageType) {
        if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER))
        {

            m_strImageMessage = "When done remove finger from sensor";
            captureStatusMessage = m_strImageMessage;
        }
    }

    @Override
    public void deviceImageResultAvailable(IBScanDevice device, IBScanDevice.ImageData image, IBScanDevice.ImageType imageType, IBScanDevice.ImageData[] splitImageArray) {

    }


    @Override
    public void deviceImageResultExtendedAvailable(IBScanDevice device, IBScanException imageStatus, IBScanDevice.ImageData image, IBScanDevice.ImageType imageType, int detectedFingerCount, IBScanDevice.ImageData[] segmentImageArray, IBScanDevice.SegmentPosition[] segmentPositionArray) {

        // Convert to Base64


        String base64Result = convertImageDataToBase64(image);
        base64FingerPrintImage = base64Result != null ? base64Result : "no base64 image";

        System.out.println("Base64 String: " + base64FingerPrintImage);
        // imageStatus value is greater than "STATUS_OK", Image acquisition successful.
        if (imageStatus == null /*STATUS_OK*/ ||
                imageStatus.getType().compareTo(IBScanException.Type.INVALID_PARAM_VALUE) > 0)
        {
            if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER))
            {
                captureStatusMessage = "first success acquistion";
            }
        }



        // imageStatus value is greater than "STATUS_OK", Image acquisition successful.
        if (imageStatus == null /*STATUS_OK*/ ||
                imageStatus.getType().compareTo(IBScanException.Type.INVALID_PARAM_VALUE) > 0)
        {
            // Image acquisition successful
            CaptureInfo info = m_vecCaptureSeq.elementAt(m_nCurrentCaptureStep);


            // NFIQ
//			if (m_chkNFIQScore.isSelected())
            {
                byte[] nfiq_score = { 0, 0, 0, 0 };
                boolean isSpoof = false;
                try
                {
                    for (int i=0, segment_pos=0; i<4; i++)
                    {
                        if (m_FingerQuality[i].ordinal() != IBScanDevice.FingerQualityState.FINGER_NOT_PRESENT.ordinal())
                        {
                            nfiq_score[i] = (byte)getIBScanDevice().calculateNfiqScore(segmentImageArray[segment_pos++]);
                        }
                    }
//                    OnMsg_SetTxtNFIQScore("" + nfiq_score[0] + "-" + nfiq_score[1] + "-" + nfiq_score[2] + "-" + nfiq_score[3]);






                }
                catch (IBScanException ibse)
                {
                    String str = "";
                    if( ibse.getType().equals(IBScanException.Type.DEVICE_LOCK_ILLEGAL_DEVICE) )
                    {
                        str = "License is not activated";
                      captureStatusMessage =  "[Error code = " + ibse.getType().toCode() + "]" + str;
                    }
                    else if (ibse.getType().equals(IBScanException.Type.PAD_PROPERTY_DISABLED))
                    {
                        captureStatusMessage = "PAD Property is not enabled\n(Resource missing)";
                    }
                    ibse.printStackTrace();
                }
            }

            if (imageStatus == null /*STATUS_OK*/)
            {
                m_strImageMessage = base64FingerPrintImage;
                _SetImageMessage(m_strImageMessage);

            }
            else
            {
                // > IBSU_STATUS_OK
                m_strImageMessage = "Acquisition Warning (Warning code = " + imageStatus.getType().toString() + ")";
                _SetImageMessage(m_strImageMessage);
                captureStatusMessage = m_strImageMessage;
                return;
            }
        }
        else
        {
            // < IBSU_STATUS_OK
            m_strImageMessage = "Acquisition failed (Error code = " + imageStatus.getType().toString() + ")";
            _SetImageMessage(m_strImageMessage);
            captureStatusMessage = m_strImageMessage;

            // Stop all of acquisition
            m_nCurrentCaptureStep = (int)m_vecCaptureSeq.size();
        }


        OnMsg_CaptureSeqNext();

    }
//    private void showToastOnUiThread(final String message, final int duration)
//    {
//        mActivity.runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                Toast toast = Toast.makeText(applicationContext, message, duration);
//                toast.show();
//            }
//        });
//    }



    @Override
    public void devicePlatenStateChanged(IBScanDevice device, IBScanDevice.PlatenState platenState) {

    }

    @Override
    public void deviceWarningReceived(IBScanDevice device, IBScanException warning) {
captureStatusMessage = "Warning received " + warning.getType().toString();
    }

    @Override
    public void devicePressedKeyButtons(IBScanDevice device, int pressedKeyButtons) {

    }

    @Override
    public void scanDeviceAttached(int deviceId) {
//        showToastOnUiThread("Device " + deviceId + " attached", Toast.LENGTH_SHORT);

        /*
         * Check whether we have permission to access this device.  Request permission so it will
         * appear as an IB scanner.
         */
        samplePermissionMessage = "checking permission";
        final boolean hasPermission = m_ibScan.hasPermission(deviceId);
        if (!hasPermission)
        {
            samplePermissionMessage = "no persmiision";
            m_ibScan.requestPermission(deviceId);
        }else{
            samplePermissionMessage = "have permission";
        }
    }

    @Override
    public void scanDeviceDetached(int deviceId) {

    }

    @Override
    public void scanDevicePermissionGranted(int deviceId, boolean granted) {

    }

    @Override
    public void scanDeviceCountChanged(int deviceCount) {

    }

    @Override
    public void scanDeviceInitProgress(int deviceIndex, int progressValue) {
//    captureStatusMessage = "Initializing device..."+ progressValue + "%";
    }

    @Override
    public void scanDeviceOpenComplete(int deviceIndex, IBScanDevice device, IBScanException exception) {

    }

    protected class CaptureInfo
    {
        String		PreCaptureMessage;		// to display on fingerprint window
        String		PostCaptuerMessage;		// to display on fingerprint window
        IBScanDevice.ImageType ImageType;				// capture mode
        int			NumberOfFinger;			// number of finger count
        String		fingerName;				// finger name (e.g left thumbs, left index ... )
        IBCommon.FingerPosition fingerPosition;      // Finger position for IBSM(Matcher) structure
    };
    protected void _SetImageMessage(String s)
    {
        m_strImageMessage = s;
    }
    protected IBScanDevice.ImageType m_ImageType;
    protected String m_SpoofThresLevel;
    private String captureStatusMessage = "first capture message";

    private IBScanDevice m_ibScanDevice;
    protected boolean 	m_bBlank = false;
    protected IBScanDevice.FingerQualityState[] m_FingerQuality = {IBScanDevice.FingerQualityState.FINGER_NOT_PRESENT, IBScanDevice.FingerQualityState.FINGER_NOT_PRESENT, IBScanDevice.FingerQualityState.FINGER_NOT_PRESENT, IBScanDevice.FingerQualityState.FINGER_NOT_PRESENT};
    protected int m_nCurrentCaptureStep = -1;
    private IBScan       m_ibScan;///< Current capture step
    protected String 	m_strImageMessage = "";
    protected IBScan getIBScan()
    {
        return (this.m_ibScan);
    }

    protected IBScanDevice getIBScanDevice()
    {
        return (this.m_ibScanDevice);
    }

    	 	private Context applicationContext;
 	private Activity mActivity;
    private static Method finger = null;
    protected Vector<CaptureInfo> m_vecCaptureSeq = new Vector<CaptureInfo>();
//	public OcraFingerPrintPluginPlugin(Context context, Activity activity) {
// 		applicationContext = context.getApplicationContext();
// 		mActivity = activity;
// 	}


    private OcraFingerPrintPlugin implementation = new OcraFingerPrintPlugin();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
    @PluginMethod()
    public void testPluginMethod(PluginCall call) {

        int number1 = call.getInt("number1");
        int number2 = call.getInt("number2");

        int sum = number1 + number2;

        JSObject ret = new JSObject();
        ret.put("value", sum);
        call.resolve(ret);
    }
    class _InitializeDeviceThreadCallback extends Thread {
        private int devIndex;

        _InitializeDeviceThreadCallback(int devIndex) {
            this.devIndex = devIndex;
        }

        @Override
        public void run() {
            try {
                m_bInitializing = true;
                Log.d("FingerprintPlugin", "Initializing device with index: " + this.devIndex);

                if (m_nDeviceLockState == DEVICE_LOCKED || m_nDeviceLockState == DEVICE_KEY_INVALID) {
                    if (m_strCustomerKey == null || m_strCustomerKey.isEmpty()) { // Check if key is null or empty
                        m_bInitializing = false;
                        captureStatusMessage = "Customer key not inserted";
                        Log.e("FingerprintPlugin", captureStatusMessage);
                        return;
                    } else { // Try to unlock with customer key
                        try {
                            getIBScan().setCustomerKey(this.devIndex, HashType.SHA256, m_strCustomerKey);
                        } catch (IBScanException ibse) {
                            captureStatusMessage = "setCustomerKey returned exception " + ibse.getType().toString() + ".";
                            Log.e("FingerprintPlugin", captureStatusMessage);
                            return;
                        }
                    }
                }

                IBScanDevice ibScanDeviceNew = getIBScan().openDevice(this.devIndex);
                setIBScanDevice(ibScanDeviceNew);
                captureStatusMessage = "Device initialized successfully";
                Log.d("FingerprintPlugin", captureStatusMessage);
            } catch (IBScanException ibse) {
                captureStatusMessage = "Failed to initialize device: " + ibse.getType().toString();
                Log.e("FingerprintPlugin", captureStatusMessage);
            } finally {
                m_bInitializing = false;
            }
        }
    }

    @PluginMethod()
    public void getDevice(PluginCall call) {
        JSObject ret = new JSObject();

        // Check if the device is already initialized and opened
        if (getIBScanDevice() == null || !getIBScanDevice().isOpened()) {
            toggleCometReader(true);
            Context context = getContext();

            // Initialize IBScan instance
            m_ibScan = IBScan.getInstance(context);
            if (m_ibScan == null) {
                Log.e("FingerprintPlugin", "Failed to get IBScan instance.");
                ret.put("value", "Failed to get IBScan instance.");
                call.resolve(ret);
                return;
            }
            m_ibScan.setScanListener(this);

            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

            if (deviceList.isEmpty()) {
                // No devices found
                ret.put("value", "No devices found!!!");
                call.resolve(ret);
                return;
            }

            // Get the first device found
            UsbDevice firstDevice = deviceList.values().iterator().next();
            Log.d("FingerprintPlugin", "Device found: " + firstDevice.getDeviceName());
            final boolean isScanDevice = IBScan.isScanDevice(firstDevice);
            if (isScanDevice) {
                samplePermissionMessage = "is scan device checking permission";
                final boolean hasPermission = usbManager.hasPermission(firstDevice);
                if (!hasPermission) {
                    captureStatusMessage = "scan no permission for " + firstDevice.getDeviceId();
                    m_ibScan.requestPermission(firstDevice.getDeviceId());
                } else {
                    samplePermissionMessage = "scan yes permission";
                }
            }
        }

        // Thread to handle device initialization and capture
        Thread captureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Run findIBMDevice in a separate thread and wait for it to complete
                    Thread findDeviceThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            findIBMDevice(0);
                        }
                    });

                    // Check if device is null or not opened, then call findIBMDevice
                    if (getIBScanDevice() == null || !getIBScanDevice().isOpened()) {
                        findDeviceThread.start();
                        findDeviceThread.join(); // Wait for findIBMDevice to complete
                    }

                    if (getIBScan() == null) {
                        ret.put("message", "no device found " + captureStatusMessage);
                        call.resolve(ret);
                        return;
                    }
                    base64FingerPrintImage = "no base64 image found";
                    m_vecCaptureSeq.clear();
                    if (!isDeviceInitialized) {
                        // Initialization failed
                        ret.put("message", "custom exception message " + captureStatusMessage);
                        call.resolve(ret);
                        return;
                    }

                    _AddCaptureSeqVector("Please put a single finger on the sensor!",
                            "Keep finger on the sensor!",
                            IBScanDevice.ImageType.FLAT_SINGLE_FINGER,
                            1,
                            "SFF_Unknown", IBCommon.FingerPosition.UNKNOWN);

                    OnMsg_CaptureSeqNext();

                    // Wait for the capture to complete and get the Base64 string
                    Thread.sleep(1000); // Adjust the delay as needed for the capture process

                    ret.put("message", captureStatusMessage);
                    ret.put("image", base64FingerPrintImage);

                    call.resolve(ret);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ret.put("message", "Capture was interrupted: " + e.toString());
                    call.resolve(ret);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("value", "Unexpected error: " + e.toString());

                    ret.put("message", captureStatusMessage + " Unexpected error: " + e.toString());
                    ret.put("base64Image", "");
                    call.resolve(ret);
                }
            }
        });

        // Start the capture thread
        captureThread.start();
    }

    public synchronized void findIBMDevice(int devIndex) {
        int maxRetries = 3; // Maximum number of retries
        int retryCount = 0;
        isDeviceInitialized = false;

        while (retryCount < maxRetries && !isDeviceInitialized) {
            try {
                m_bInitializing = true;

                if (m_nDeviceLockState == DEVICE_LOCKED || m_nDeviceLockState == DEVICE_KEY_INVALID) {
                    if (m_strCustomerKey.isEmpty()) {
                        m_bInitializing = false;
                        captureStatusMessage = "Customer key not inserted";
                        return;
                    } else {
                        try {
                            getIBScan().setCustomerKey(devIndex, HashType.SHA256, m_strCustomerKey);
                        } catch (IBScanException ibse) {
                            captureStatusMessage = "setCustomerKey returned exception " + ibse.getType().toString() + ".";
                        }
                    }
                }

                captureStatusMessage = "initializing device with device " + m_ibScan.toString();

                IBScanDevice ibScanDeviceNew = getIBScan().openDevice(devIndex);
                if (ibScanDeviceNew == null) {
                    captureStatusMessage = "no ib scan device found ibscandevice new";
                } else {
                    setIBScanDevice(ibScanDeviceNew);
                    captureStatusMessage = "Device initialized successfully";
                    isDeviceInitialized = true;
                }
                m_bInitializing = false;
            } catch (IBScanException ibse) {
                m_bInitializing = false;
                retryCount++;

                switch (ibse.getType()) {
                    case DEVICE_ACTIVE:
                        captureStatusMessage = "[Error Code =-203] Device initialization failed because in use by another thread/process.";
                        break;
                    case USB20_REQUIRED:
                        captureStatusMessage = "[Error Code =-209] Device initialization failed because SDK only works with USB 2.0.";
                        break;
                    case DEVICE_HIGHER_SDK_REQUIRED:
                        try {
                            String m_minSDKVersion = getIBScan().getRequiredSDKVersion(devIndex);
                            captureStatusMessage = "[Error Code =-214] Device initialization failed because SDK Version " + m_minSDKVersion + " is required at least.";
                        } catch (IBScanException ibse1) {
                            captureStatusMessage = "Failed to get required SDK version.";
                        }
                        break;
                    default:
                        try {
                            captureStatusMessage = "[Error code = " + ibse.getType().toCode() + "] Device initialization failed. " + getIBScan().getErrorString(ibse.getType().toCode());
                        } catch (IBScanException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(1000); // Wait before retrying
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!isDeviceInitialized) {
            captureStatusMessage = "Failed to initialize device after " + maxRetries + " retries.";
        }
    }

    protected void _ReleaseDevice() throws IBScanException
    {
        if (getIBScanDevice() != null)
        {
            if (getIBScanDevice().isOpened() == true)
            {
                getIBScanDevice().close();
                setIBScanDevice(null);
            }
        }

        m_nCurrentCaptureStep = -1;
        m_bInitializing = false;
    }

    //to turn on the fingerprint device
    public static void toggleCometReader(boolean state) {
        try {
            if (finger == null) {
                finger = Class.forName("android.os.SystemProperties")
                        .getMethod("EnableFingerpint", String.class, boolean.class);
            }
            finger.invoke(null, "finger", state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void _AddCaptureSeqVector(String PreCaptureMessage, String PostCaptuerMessage,
                                        IBScanDevice.ImageType imageType, int NumberOfFinger, String fingerName, IBCommon.FingerPosition fingerPosition)
    {
//        captureStatusMessage = PreCaptureMessage;
        CaptureInfo info = new CaptureInfo();
        info.PreCaptureMessage = PreCaptureMessage;
        info.PostCaptuerMessage = PostCaptuerMessage;
        info.ImageType = imageType;
        info.NumberOfFinger = NumberOfFinger;
        info.fingerName = fingerName;
        info.fingerPosition = fingerPosition;
        m_vecCaptureSeq.addElement(info);
//        captureStatusMessage = PostCaptuerMessage;
    }
    private void OnMsg_CaptureSeqNext() {
        if (getIBScanDevice() == null) {
            captureStatusMessage = "no device ib device found dude!!";
            return;
        };

        m_bBlank = false;
        for (int i = 0; i < 4; i++) {
            m_FingerQuality[i] = IBScanDevice.FingerQualityState.FINGER_NOT_PRESENT;
        }

        m_nCurrentCaptureStep++;
        if (m_nCurrentCaptureStep >= m_vecCaptureSeq.size()) {
            // All of capture sequence completely
            captureStatusMessage = "Capture sequence completed";
            m_nCurrentCaptureStep = -1;
            return;
        }

        try {
            getIBScanDevice().setProperty(IBScanDevice.PropertyId.ENABLE_SPOOF, "FALSE");

            // Make capture delay for display result image on multi capture mode (500 ms)
            if (m_nCurrentCaptureStep > 0) {
                _Sleep(500);
                m_strImageMessage = "";
                captureStatusMessage = "on timer";
            }

            CaptureInfo info = m_vecCaptureSeq.elementAt(m_nCurrentCaptureStep);

            IBScanDevice.ImageResolution imgRes = IBScanDevice.ImageResolution.RESOLUTION_500;
            boolean bAvailable = getIBScanDevice().isCaptureAvailable(info.ImageType, imgRes);
            if (!bAvailable) {
                captureStatusMessage = "The capture mode (" + info.ImageType + ") is not available";
                m_nCurrentCaptureStep = -1;
                return;
            }

            // Start capture
            int captureOptions = IBScanDevice.OPTION_AUTO_CONTRAST | IBScanDevice.OPTION_AUTO_CAPTURE | IBScanDevice.OPTION_IGNORE_FINGER_COUNT;
            getIBScanDevice().beginCaptureImage(info.ImageType, imgRes, captureOptions);

            String strMessage = info.PreCaptureMessage;
            captureStatusMessage = strMessage;
            m_strImageMessage = strMessage;
            m_ImageType = info.ImageType;

        } catch (IBScanException ibse) {
            ibse.printStackTrace();
            captureStatusMessage = "Failed to execute beginCaptureImage()";
            m_nCurrentCaptureStep = -1;
        }
    }
    protected void setIBScanDevice(IBScanDevice ibScanDevice)
    {
        m_ibScanDevice = ibScanDevice;
        if (ibScanDevice != null)
        {
            ibScanDevice.setScanDeviceListener(this);
        }
    }

}



