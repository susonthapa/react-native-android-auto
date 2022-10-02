import { debounce, cloneDeepWith } from "lodash";
import { NativeEventEmitter, NativeModules } from "react-native";
const invalidate = debounce((screenName) => {
    NativeModules.CarModule.invalidate(screenName);
}, 50);
const eventEmitter = new NativeEventEmitter(NativeModules.CarModule);
function prepareTemplate(name, template) {
    let currentIndex = 0;
    const callbacks = new Map();
    const templateClone = cloneDeepWith(template, (value) => {
        if (typeof value === "function") {
            currentIndex++;
            callbacks.set(currentIndex, value);
            return currentIndex;
        }
        return undefined;
    });
    const callbackFromNative = ({ id, ...event }) => {
        NativeModules.CarModule.setEventCallback(name, callbackFromNative);
        const callback = callbacks.get(id);
        if (callback) {
            callback(event);
        }
    };
    return [name, templateClone, callbackFromNative];
}
export const AndroidAutoModule = {
    init() { },
    eventEmitter,
    mapNavigate(address) {
        NativeModules.CarModule.mapNavigate(address);
    },
    reload() {
        NativeModules.CarModule.reload();
    },
    finishCarApp() {
        NativeModules.CarModule.finishCarApp();
    },
    invalidate,
    setTemplate: debounce((name, template) => {
        NativeModules.CarModule.setTemplate(...prepareTemplate(name, template));
        invalidate(name);
    }, 50),
    pushScreen: (name, template) => {
        NativeModules.CarModule.pushScreen(...prepareTemplate(name, template));
    },
    popScreen: () => {
        NativeModules.CarModule.popScreen();
    },
    toast: (text, duration = 1) => {
        NativeModules.CarModule.toast(text, duration);
    },
};
