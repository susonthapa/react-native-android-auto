import type React from "react";
import type { ImageResolvedAssetSource } from "react-native";

type HeaderAction = "back" | "app_icon";
type CarColor =
  | "blue"
  | "green"
  | "primary"
  | "red"
  | "secondary"
  | "yellow"
  | "default";
type PressHandler = (event: {}) => any
interface PlaceMetadata {
  type: "place";
  distance: Distance,
  icon: ImageResolvedAssetSource,
  latitude: number;
  longitude: number;
}

interface ActionStrip {
  actions: Omit<Action, "type">[];
}

type MapActionStrip = {
  actions: (Omit<Action, "type" | "title" | "icon"> & { icon: NonNullable<Action['icon']> })[],
}

type Step = {
  lane: {
    shape: number,
    isRecommended: boolean,
  },
  cue?: string,
  lanesImage: ImageResolvedAssetSource,
  maneuver?: {
    type: number,
    icon: ImageResolvedAssetSource,
    roundaboutExitAngle: number,
    roundaboutExitNumber: number,
  },
  road?: string,
}

type Distance = {
  displayDistance: number,
  displayUnit: number,
}

type RoutingInfo = {
  step: Step,
  isLoading: boolean,
  distance: Distance,
  junctionImage?: ImageResolvedAssetSource,
  nextStep?: Step,
}

type MessageInfo = {
  title: string,
  icon?: ImageResolvedAssetSource,
}

type NavigationInfo = {
  type: 'routingInfo' | 'messageInfo',
  info: RoutingInfo | MessageInfo,
}

type TravelEstimate = {
  remainingDistance: Distance,
  destinationTime: {
    timeSinceEpochMillis: number,
    id: string,
  },
  remainingTimeSeconds: number,
}

type Metadata = PlaceMetadata;

interface CommonAttributes {
  key?: string | number;
}

interface Action extends CommonAttributes {
  type: "action";
  title?: string;
  icon?: ImageResolvedAssetSource;
  backgroundColor?: CarColor;
  onPress?: (event: {}) => any;
}

interface Row extends CommonAttributes {
  type: "row";
  title: string;
  texts?: string[];
  image?: ImageResolvedAssetSource;
  onPress?: PressHandler;
  metadata?: Metadata | undefined;
}

interface GridItem extends CommonAttributes {
  type: "grid-item",
  title: string,
  text?: string,
  image?: ImageResolvedAssetSource,
  onPress?: PressHandler,
}

interface ItemList extends CommonAttributes {
  type: "item-list";
  header: string;
  children: Row[];
}

interface ListTemplate extends CommonAttributes {
  type: "list-template";
  title: string;
  headerAction?: HeaderAction;
  isLoading?: boolean;
  actionStrip?: ActionStrip;
  children: ItemList[];
}

interface GridTemplate extends CommonAttributes {
  type: "grid-template",
  isLoading?: boolean,
  title?: string,
  noItemMessage?: string,
  headerAction?: HeaderAction,
  actionStrip?: ActionStrip,
  children: GridItem[],
}

interface PlaceListMapTemplate extends CommonAttributes {
  type: "place-list-map-template";
  title: string;
  headerAction?: HeaderAction;
  isLoading?: boolean;
  actionStrip?: ActionStrip;
  children: Row[];
}

interface PaneTemplate extends CommonAttributes {
  type: "pane-template";
  title: string;
  headerAction?: HeaderAction;
  actionStrip?: ActionStrip;
  children: ItemList[];
}

interface NavigationTemplate extends CommonAttributes {
  type: "navigation-template",
  id: string,
  actionStrip: ActionStrip,
  mapActionStrip?: MapActionStrip,
  navigationInfo?: NavigationInfo,
  destinationTravelEstimate?: TravelEstimate,
  component: React.ComponentType<any>,
}

interface Screen extends CommonAttributes {
  type: "screen";
  name: string;
  render: (props?: any) => React.ReactElement<AndroidAutoTemplate>;
  children: AndroidAutoTemplate[] | AndroidAutoTemplate;
}

interface ScreenManager extends CommonAttributes {
  type: "screen-manager";
  children: Screen[];
}

export type AndroidAutoTemplate =
  | PaneTemplate
  | ListTemplate
  | GridTemplate
  | NavigationTemplate
  | PlaceListMapTemplate;

export type ExtractElementByType<Type extends AndroidAutoElement["type"]> =
  Extract<AndroidAutoElement, { type: Type }>;
export type AndroidAutoElement =
  | AndroidAutoTemplate
  | Row
  | GridItem
  | ItemList
  | ScreenManager
  | Screen
  | Action;
export type ElementType = AndroidAutoElement["type"];
export interface Route {
  name: string;
  routeParams?: any;
  render?: React.FC;
}
export interface RootContainer {
  type: "root-container";
  stack: Route[];
  prevStack: Route[];
  children?: AndroidAutoElement[];
}