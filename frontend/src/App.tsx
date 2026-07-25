import { Route, Routes } from 
"react-router-dom";
import HomePage from "./pages/home/HomePage.tsx";
import MainLayout from "./layout/MainLayout.tsx";
import ChatPage from "./pages/chat/ChatPage.tsx";
import AlbumPage from "./pages/album/AlbumPage.tsx";
import AdminPage from "./pages/admin/AdminPage.tsx";
import { Toaster } from "react-hot-toast";


function App() {
  
  return (
    <>
      <Routes>
        
        <Route path="/admin" element={<AdminPage />} />

        <Route element={<MainLayout/>}>
          <Route path="/" element={<HomePage/>} />
          <Route path="/chat" element={<ChatPage/>} />
          <Route path="/albums/:albumId" element={<AlbumPage/>} />
        </Route>
      </Routes>

      <Toaster />
    </>
    
  );
}

export default App;
