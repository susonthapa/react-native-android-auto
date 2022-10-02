import React from 'react';
import { Screen, ScreenManager } from 'react-native-android-auto';
const DeliveryListScreen = () => {
    return (React.createElement("pane-template", { title: 'Shopify Local Delivery' },
        React.createElement("item-list", { header: "Delivery Lists" },
            React.createElement("row", { key: 1, title: "Today's Delivery", texts: ['Delivery 1'] }),
            React.createElement("action", { title: "Add Delivery" }))));
};
const Main = () => {
    return (React.createElement("list-template", { title: 'Shopify Local Delivery test', isLoading: false },
        React.createElement("item-list", { header: "Delivery Lists" },
            React.createElement("row", { key: 1, title: "Today's Delivery", texts: ['Delivery 1'] }))));
};
const RootApp = () => {
    return (React.createElement(ScreenManager, null,
        React.createElement(Screen, { name: "root", render: Main }),
        React.createElement(Screen, { name: "deliveryList", render: DeliveryListScreen })));
};
export default RootApp;
// const styles = StyleSheet.create({});
