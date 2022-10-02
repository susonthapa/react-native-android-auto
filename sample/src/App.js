import React from 'react';
import { SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, useColorScheme, View } from 'react-native';
import { Colors, DebugInstructions, Header, LearnMoreLinks, ReloadInstructions } from 'react-native/Libraries/NewAppScreen';
const Section = ({ children, title }) => {
    const isDarkMode = useColorScheme() === 'dark';
    return (React.createElement(View, { style: styles.sectionContainer },
        React.createElement(Text, { style: [
                styles.sectionTitle,
                {
                    color: isDarkMode ? Colors.white : Colors.black,
                },
            ] }, title),
        React.createElement(Text, { style: [
                styles.sectionDescription,
                {
                    color: isDarkMode ? Colors.light : Colors.dark,
                },
            ] }, children)));
};
const App = () => {
    const isDarkMode = useColorScheme() === 'dark';
    const backgroundStyle = {
        backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
    };
    return (React.createElement(SafeAreaView, { style: backgroundStyle },
        React.createElement(StatusBar, { barStyle: isDarkMode ? 'light-content' : 'dark-content' }),
        React.createElement(ScrollView, { contentInsetAdjustmentBehavior: "automatic", style: backgroundStyle },
            React.createElement(Header, null),
            React.createElement(View, { style: {
                    backgroundColor: isDarkMode ? Colors.black : Colors.white,
                } },
                React.createElement(Section, { title: "Step One" },
                    "Edit ",
                    React.createElement(Text, { style: styles.highlight }, "App.js"),
                    " to change this screen and then come back to see your edits."),
                React.createElement(Section, { title: "See Your Changes" },
                    React.createElement(ReloadInstructions, null)),
                React.createElement(Section, { title: "Debug" },
                    React.createElement(DebugInstructions, null)),
                React.createElement(Section, { title: "Learn More" }, "Read the docs to discover what to do next:"),
                React.createElement(LearnMoreLinks, null)))));
};
const styles = StyleSheet.create({
    sectionContainer: {
        marginTop: 32,
        paddingHorizontal: 24,
    },
    sectionTitle: {
        fontSize: 24,
        fontWeight: '600',
    },
    sectionDescription: {
        marginTop: 8,
        fontSize: 18,
        fontWeight: '400',
    },
    highlight: {
        fontWeight: '700',
    },
});
export default App;
