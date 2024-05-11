import Foundation

@objc public class OcraFingerPrintPlugin: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
