import { NativeModules } from "react-native";

const { AndroidAuto } = NativeModules;

export default AndroidAuto;

export {
  Screen,
  ScreenManager,
  useCarNavigation,
} from "./src/AndroidAutoReact";
export { render } from "./src/AndroidAutoReconciler";
export { AndroidAutoModule } from "./src/AndroidAuto";
import "./src/android-auto.global";
