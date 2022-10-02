import React from "react";
const NavigationContext = React.createContext({
    push: (() => { }),
    pop: (() => { }),
    registerScreen: (() => { }),
    stack: [],
});
export function RootView(props) {
    const [stack, setStack] = React.useState([]);
    const screens = React.useRef([]);
    const push = React.useCallback((routeName, params) => {
        setStack((prev) => {
            const screen = screens.current.find(({ name }) => name === routeName);
            if (!screen) {
                console.log(`Cannot navigatie to ${routeName}: Route does not exist`);
                return prev;
            }
            const newState = [
                ...prev,
                {
                    name: routeName,
                    key: prev.length,
                    render: screen.render,
                    routeParams: params ?? {},
                },
            ];
            props.containerInfo.stack = newState;
            return newState;
        });
    }, [props.containerInfo]);
    const pop = React.useCallback(() => {
        setStack((prev) => {
            if (prev.length === 1) {
                return prev;
            }
            const newState = prev.slice(0, -1);
            props.containerInfo.stack = newState;
            return newState;
        });
    }, [props.containerInfo]);
    const registerScreen = React.useCallback((screen) => {
        screens.current.push(screen);
        if (screen.name === "root") {
            push("root");
        }
    }, [push]);
    const navigationContextValue = React.useMemo(() => {
        return {
            push,
            pop,
            registerScreen,
            stack,
        };
    }, [push, pop, registerScreen, stack]);
    return (React.createElement(NavigationContext.Provider, { value: navigationContextValue }, props.children));
}
export const useCarNavigation = () => React.useContext(NavigationContext);
export const Screen = React.memo(function Screen({ name, render, }) {
    console.log("AndroidAuto: Screen rendering");
    const navigation = useCarNavigation();
    React.useEffect(() => {
        navigation.registerScreen({
            name,
            render,
            type: "screen",
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);
    return null;
});
export const ScreenManager = React.memo(function ScreenManager({ children, }) {
    console.log("AndroidAuto: Screen Manager rendering");
    const { stack } = useCarNavigation();
    return (React.createElement(React.Fragment, null,
        children,
        stack.map(({ render, routeParams, ...item }) => {
            return React.createElement("screen", item, render && React.createElement(render, { routeParams }));
        })));
});
