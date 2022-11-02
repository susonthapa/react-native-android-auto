import type React from "react";
import type { AndroidAutoElement, ExtractElementByType } from "./types";

type NativeToJSXElement<Type extends AndroidAutoElement["type"]> = Omit<
  ExtractElementByType<Type>,
  "children" | "type"
> & {
  children?: React.ReactNode;
};

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace JSX {
    interface IntrinsicElements {
      "list-template": NativeToJSXElement<"list-template">;
      "item-list": NativeToJSXElement<"item-list">;
      "place-list-map-template": NativeToJSXElement<"place-list-map-template">;
      action: NativeToJSXElement<"action">;
      "pane-template": NativeToJSXElement<"pane-template">;
      row: NativeToJSXElement<"row">;
      "grid-item": NativeToJSXElement<"grid-item">,
      "navigation-template": NativeToJSXElement<"navigation-template">;
      "grid-template": NativeToJSXElement<"grid-template">
    }
  }
}

export {};
