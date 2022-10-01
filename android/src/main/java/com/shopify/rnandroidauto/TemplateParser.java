package com.shopify.rnandroidauto;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.LatLng;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Metadata;
import com.google.android.libraries.car.app.model.Pane;
import com.google.android.libraries.car.app.model.PaneTemplate;
import com.google.android.libraries.car.app.model.Place;
import com.google.android.libraries.car.app.model.PlaceListMapTemplate;
import com.google.android.libraries.car.app.model.PlaceMarker;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import java.util.ArrayList;

public class TemplateParser {

  private ReactCarRenderContext mReactCarRenderContext;

  TemplateParser(ReactCarRenderContext reactCarRenderContext) {
    mReactCarRenderContext = reactCarRenderContext;
  }

  public Template parseTemplate(ReadableMap renderMap) {
    String type = renderMap.getString("type");

    switch (type) {
      case "list-template":
        return parseListTemplateChildren(renderMap);
      case "place-list-map-template":
        return parsePlaceListMapTemplate(renderMap);
      case "pane-template":
        return parsePaneTemplate(renderMap);
      default:
        return PaneTemplate
          .builder(Pane.builder().setIsLoading(true).build())
          .setTitle("Pane Template")
          .build();
    }
  }

  private PaneTemplate parsePaneTemplate(ReadableMap map) {
    Pane.Builder paneBuilder = Pane.builder();

    ReadableArray children = map.getArray("children");

    boolean loading;

    try {
      loading = map.getBoolean("isLoading");
    } catch (NoSuchKeyException e) {
      loading = children == null || children.size() == 0;
    }

    paneBuilder.setIsLoading(loading);

    if (!loading) {
      ArrayList<Action> actions = new ArrayList();
      Log.d("ReactAUTO", "Found children: " + children);

      for (int i = 0; i < children.size(); i++) {
        ReadableMap child = children.getMap(i);
        String type = child.getString("type");
        Log.d("ReactAUTO", "Adding child with type " + type + " to row");

        if (type.equals("row")) {
          Log.d("ReactAUTO", "Parsing row");
          paneBuilder.addRow(buildRow(child));
        } else if (type.equals("action")) {
          Log.d("ReactAUTO", "Parsing action");
          actions.add(parseAction(child));
        } else {
          Log.d("ReactAUTO", "Unknown type " + type);
        }
      }

      if (actions.size() > 0) {
        Log.d("ReactAUTO", "Setting actions to pane: " + actions);
        paneBuilder.setActions(actions);
      }
    }

    PaneTemplate.Builder builder = PaneTemplate.builder(paneBuilder.build());

    String title = map.getString("title");
    if (title == null || title.length() == 0) {
      builder.setTitle("<No Title>");
    } else {
      builder.setTitle(title);
    }

    try {
      builder.setHeaderAction(getHeaderAction(map.getString("headerAction")));
    } catch (NoSuchKeyException e) {}

    try {
      ReadableMap actionStripMap = map.getMap("actionStrip");
      builder.setActionStrip(parseActionStrip(actionStripMap));
    } catch (NoSuchKeyException e) {}

    return builder.build();
  }

  private ActionStrip parseActionStrip(ReadableMap map) {
    ActionStrip.Builder builder = ActionStrip.builder();

    if (map != null) {
      ReadableArray actions = map.getArray("actions");

      for (int i = 0; i < actions.size(); i++) {
        ReadableMap actionMap = actions.getMap(i);
        Action action = parseAction(actionMap);
        builder.addAction(action);
      }
      return builder.build();
    } else {
      return null;
    }
  }

  private Action parseAction(ReadableMap map) {
    Action.Builder builder = Action.builder();

    if (map != null) {
      builder.setTitle(map.getString("title"));
      try {
        builder.setBackgroundColor(getColor(map.getString("backgroundColor")));
      } catch (NoSuchKeyException e) {
        Log.d("ReactAUTO", "Couldn't set background color", e);
      }

      try {
        int onPress = map.getInt("onPress");

        builder.setOnClickListener(
          () -> {
            invokeCallback(onPress);
          }
        );
      } catch (NoSuchKeyException e) {
        Log.d("ReactAUTO", "Couldn't parseAction", e);
      }
    } else {
      Log.d("ReactAUTO", "No readable map supplied to parseAction");
    }

    return builder.build();
  }

  private CarColor getColor(String colorName) {
    if (colorName != null) {
      switch (colorName) {
        case "blue":
          return CarColor.BLUE;
        case "green":
          return CarColor.GREEN;
        case "primary":
          return CarColor.PRIMARY;
        case "red":
          return CarColor.RED;
        case "secondary":
          return CarColor.SECONDARY;
        case "yellow":
          return CarColor.YELLOW;
        default:
        case "default":
          return CarColor.DEFAULT;
      }
    } else {
      return CarColor.DEFAULT;
    }
  }

  private PlaceListMapTemplate parsePlaceListMapTemplate(ReadableMap map) {
    PlaceListMapTemplate.Builder builder = PlaceListMapTemplate.builder();

    builder.setTitle(map.getString("title"));
    ReadableArray children = map.getArray("children");

    try {
      builder.setHeaderAction(getHeaderAction(map.getString("headerAction")));
    } catch (NoSuchKeyException e) {}

    boolean loading;

    try {
      loading = map.getBoolean("isLoading");
    } catch (NoSuchKeyException e) {
      loading = children == null || children.size() == 0;
    }

    Log.d("ReactAUTO", "Rendering " + (loading ? "Yes" : "No"));
    builder.setIsLoading(loading);

    if (!loading) {
      ItemList.Builder itemListBuilder = ItemList.builder();

      for (int i = 0; i < children.size(); i++) {
        ReadableMap child = children.getMap(i);
        String type = child.getString("type");
        Log.d("ReactAUTO", "Adding " + type + " to row");

        if (type.equals("row")) {
          itemListBuilder.addItem(buildRow(child));
        }
      }

      builder.setItemList(itemListBuilder.build());
    }

    try {
      ReadableMap actionStripMap = map.getMap("actionStrip");
      builder.setActionStrip(parseActionStrip(actionStripMap));
    } catch (NoSuchKeyException e) {}

    return builder.build();
  }

  private ListTemplate parseListTemplateChildren(ReadableMap map) {
    ReadableArray children = map.getArray("children");

    ListTemplate.Builder builder = ListTemplate.builder();

    boolean loading;

    try {
      loading = map.getBoolean("isLoading");
    } catch (NoSuchKeyException e) {
      loading = children.size() == 0;
    }

    builder.setIsLoading(loading);

    if (!loading) {
      for (int i = 0; i < children.size(); i++) {
        ReadableMap child = children.getMap(i);
        String type = child.getString("type");
        if (type.equals("item-list")) {
          builder.addList(
            parseItemListChildren(child),
            child.getString("header")
          );
        }
      }
    }

    try {
      builder.setHeaderAction(getHeaderAction(map.getString("headerAction")));
    } catch (NoSuchKeyException e) {}

    try {
      ReadableMap actionStripMap = map.getMap("actionStrip");
      builder.setActionStrip(parseActionStrip(actionStripMap));
    } catch (NoSuchKeyException e) {}

    builder.setTitle(map.getString("title"));

    return builder.build();
  }

  private ItemList parseItemListChildren(ReadableMap itemList) {
    ItemList.Builder builder = ItemList.builder();

    if (itemList != null) {
      ReadableArray children = itemList.getArray("children");
      for (int i = 0; i < children.size(); i++) {
        ReadableMap child = children.getMap(i);
        String type = child.getString("type");
        if (type.equals("row")) {
          builder.addItem(buildRow(child));
        }
      }
    }

    try {
      builder.setNoItemsMessage(itemList.getString("noItemsMessage"));
    } catch (NoSuchKeyException e) {}

    return builder.build();
  }

  @NonNull
  private Row buildRow(ReadableMap rowRenderMap) {
    Row.Builder builder = Row.builder();

    builder.setTitle(rowRenderMap.getString("title"));

    try {
      ReadableArray texts = rowRenderMap.getArray("texts");

      for (int i = 0; texts != null && i < texts.size(); i++) {
         if (rowRenderMap.getString("type") == "place") {
          int distanceKm = 1000;
          SpannableString description = new SpannableString("   \u00b7 " + texts.getString(i));
          description.setSpan(
                  DistanceSpan.create(Distance.create(distanceKm, Distance.UNIT_MILES)),
                  0,
                  1,
                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          description.setSpan(
                  ForegroundCarColorSpan.create(CarColor.BLUE), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          builder.addText(description);
        } else {
          builder.addText(texts.getString(i));
        }
      }
    } catch (NoSuchKeyException e) {}

    try {
      int onPress = rowRenderMap.getInt("onPress");

      builder.setIsBrowsable(true);

      builder.setOnClickListener(
        () -> {
          invokeCallback(onPress);
        }
      );
    } catch (NoSuchKeyException e) {}

    try {
      builder.setMetadata(parseMetaData(rowRenderMap.getMap("metadata")));
    } catch (NoSuchKeyException e) {}

    return builder.build();
  }

  private Metadata parseMetaData(ReadableMap map) {
    if (map != null) {
      switch (map.getString("type")) {
        case "place":
          return Metadata.ofPlace(
            Place
              .builder(
                LatLng.create(
                  map.getDouble("latitude"),
                  map.getDouble("longitude")
                )
              )
              .setMarker(PlaceMarker.getDefault())
              .build()
          );
        default:
          return null;
      }
    } else {
      return null;
    }
  }

  private Action getHeaderAction(String actionName) {
    if (actionName == null) {
      return null;
    } else {
      switch (actionName) {
        case "back":
          return Action.BACK;
        case "app_icon":
          return Action.APP_ICON;
        default:
          return null;
      }
    }
  }

  private void invokeCallback(int callbackId) {
    invokeCallback(callbackId, null);
  }

  private void invokeCallback(int callbackId, WritableNativeMap params) {
    if (params == null) {
      params = new WritableNativeMap();
    }

    params.putInt("id", callbackId);
    params.putString("screen", mReactCarRenderContext.getScreenMarker());

    mReactCarRenderContext.getEventCallback().invoke(params);
  }
}