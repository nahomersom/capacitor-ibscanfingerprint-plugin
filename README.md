# ocra-fingerprint-plugin

plugin

## Install

```bash
npm install ocra-fingerprint-plugin
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`testPluginMethod(...)`](#testpluginmethod)
* [`getDevice()`](#getdevice)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### testPluginMethod(...)

```typescript
testPluginMethod(options: { number1: any; number2: any; }) => Promise<{ value: any; }>
```

| Param         | Type                                         |
| ------------- | -------------------------------------------- |
| **`options`** | <code>{ number1: any; number2: any; }</code> |

**Returns:** <code>Promise&lt;{ value: any; }&gt;</code>

--------------------


### getDevice()

```typescript
getDevice() => Promise<{ image: string; message: string; }>
```

**Returns:** <code>Promise&lt;{ image: string; message: string; }&gt;</code>

--------------------

</docgen-api>
