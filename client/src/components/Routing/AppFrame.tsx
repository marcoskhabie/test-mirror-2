import React, {ReactNode} from "react";
import AppHeader from "../common/AppHeader/AppHeader";

type AppFrame = {
    children: ReactNode
}

export const AppFrame = ({children}: AppFrame) => (
    <div className="app-body">
        <div className="app-top-box">
            <AppHeader/>
        </div>
        {children}
    </div>
)
