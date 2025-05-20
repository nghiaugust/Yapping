import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import 'antd/dist/reset.css'
import {ConfigProvider} from 'antd';
//import "./assets/styles/cusom.css"

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider theme={{
      components: {
        Menu: {
          itemMarginBlock: 20,
          itemHeight: 60,
        }
      }
    }}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ConfigProvider>
  </StrictMode>
)
