package com.reactnativeandroidauto;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.PlaceListMapTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.SectionedItemList;
import androidx.car.app.model.Template;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;

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
                return new PaneTemplate.Builder(
                        new Pane.Builder().setLoading(true).build()
                ).setTitle("Pane Template").build();
        }
    }

    private PaneTemplate parsePaneTemplate(ReadableMap map) {
        Pane.Builder paneBuilder = new Pane.Builder();

        ReadableArray children = map.getArray("children");

        boolean loading;

        try {
            loading = map.getBoolean("isLoading");
        } catch (Exception e) {
            loading = children == null || children.size() == 0;
        }

        paneBuilder.setLoading(loading);

        ArrayList<Action> actions = new ArrayList();

        if (!loading) {
            for (int i = 0; i < children.size(); i++) {
                ReadableMap child = children.getMap(i);
                String type = child.getString("type");
                Log.d("AUTO", "Adding child to row");

                if (type.equals("row")) {
                    paneBuilder.addRow(buildRow(child));
                }

                if (type.equals("action")) {
                    actions.add(parseAction(child));
                }
            }

            for (int i = 0; i < actions.size(); i++) {
                paneBuilder.addAction(actions.get(i));
            }
        }

        PaneTemplate.Builder builder = new PaneTemplate.Builder(paneBuilder.build());

        String title = map.getString("title");
        if (title == null || title.length() == 0) {
            builder.setTitle("<No Title>");
        } else {
            builder.setTitle(title);
        }

        try {
            builder.setHeaderAction(getHeaderAction(map.getString("headerAction")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ReadableMap actionStripMap = map.getMap("actionStrip");
            builder.setActionStrip(parseActionStrip(actionStripMap));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return builder.build();
    }

    private ActionStrip parseActionStrip(ReadableMap map) {
        ActionStrip.Builder builder = new ActionStrip.Builder();

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
        Action.Builder builder = new Action.Builder();

        if (map != null) {
            builder.setTitle(map.getString("title"));
            try {
                builder.setBackgroundColor(getColor(map.getString("backgroundColor")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int onPress = map.getInt("onPress");

                builder.setOnClickListener(() -> {
                    invokeCallback(onPress);
                });
            } catch (Exception e) {
                Log.d("AUTO", "Couldn't parseAction", e);
                e.printStackTrace();
            }
        }

        return builder.build();
    }

    private CarColor getColor(String colorName) {
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
    }

    private PlaceListMapTemplate parsePlaceListMapTemplate(ReadableMap map) {
        PlaceListMapTemplate.Builder builder = new PlaceListMapTemplate.Builder();

        builder.setTitle(map.getString("title"));
        ReadableArray children = map.getArray("children");


        try {
            builder.setHeaderAction(getHeaderAction(map.getString("headerAction")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean loading;

        try {
            loading = map.getBoolean("isLoading");
        } catch (Exception e) {
            loading = children == null || children.size() == 0;
        }

        Log.d("AUTO", "Rendering " + (loading ? "Yes" : "No"));
        builder.setLoading(loading);

        if (!loading) {
            ItemList.Builder itemListBuilder = new ItemList.Builder();

            for (int i = 0; i < children.size(); i++) {
                ReadableMap child = children.getMap(i);
                String type = child.getString("type");
                Log.d("AUTO", "Adding " + type + " to row");

                if (type.equals("row")) {
                    itemListBuilder.addItem(buildRow(child));
                }
            }

            builder.setItemList(itemListBuilder.build());
        }


        try {
            ReadableMap actionStripMap = map.getMap("actionStrip");
            builder.setActionStrip(parseActionStrip(actionStripMap));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.build();
    }

    private ListTemplate parseListTemplateChildren(ReadableMap map) {
        ReadableArray children = map.getArray("children");

        ListTemplate.Builder builder = new ListTemplate.Builder();

        boolean loading;

        try {
            loading = map.getBoolean("isLoading");
        } catch (Exception e) {
            loading = children.size() == 0;
        }

        builder.setLoading(loading);

        if (!loading) {
            for (int i = 0; i < children.size(); i++) {
                ReadableMap child = children.getMap(i);
                String type = child.getString("type");
                if (type.equals("item-list")) {
                    builder.addSectionedList(SectionedItemList.create(parseItemListChildren(child), child.getString("header")));
                }
            }
        }

        try {
            builder.setHeaderAction(getHeaderAction(map.getString("headerAction")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ReadableMap actionStripMap = map.getMap("actionStrip");
            builder.setActionStrip(parseActionStrip(actionStripMap));
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.setTitle(map.getString("title"));

        return builder.build();
    }

    private ItemList parseItemListChildren(ReadableMap itemList) {
        ReadableArray children = itemList.getArray("children");
        ItemList.Builder builder = new ItemList.Builder();

        for (int i = 0; i < children.size(); i++) {
            ReadableMap child = children.getMap(i);
            String type = child.getString("type");
            if (type.equals("row")) {
                builder.addItem(buildRow(child));
            }
        }

        try {
            builder.setNoItemsMessage(itemList.getString("noItemsMessage"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.build();
    }

    @NonNull
    private Row buildRow(ReadableMap rowRenderMap) {
        Row.Builder builder = new Row.Builder();

        builder.setTitle(rowRenderMap.getString("title"));

        try {
            ReadableArray texts = rowRenderMap.getArray("texts");

            for (int i = 0; i < texts.size(); i++) {
                builder.addText(texts.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int onPress = rowRenderMap.getInt("onPress");

            builder.setBrowsable(true);

            builder.setOnClickListener(() -> {
                invokeCallback(onPress);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            builder.setMetadata(parseMetaData(rowRenderMap.getMap("metadata")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.build();
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
