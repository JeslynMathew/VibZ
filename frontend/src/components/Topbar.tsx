import { LayoutDashboardIcon } from "lucide-react";
import { Link } from "react-router-dom";
import SignInOAuthButtons from "./SignInOAuthButtons";
import { useAuthStore } from "@/stores/useAuthStore";
import { buttonVariants } from "./ui/button";
import { cn } from "@/lib/utils";
import { Button } from "./ui/button";
const Topbar = () => {
    const {isAdmin,user,signOut}=useAuthStore();
    console.log({isAdmin});
  return (
    <div className="flex items-center justify-between p-4 sticky top-0 bg-zinc-900/75 backdrop-blur-md z-10">
        <div className="flex gap-2 items-center ">
            <img src='/image.png' className='size-9 w-25' alt='Music logo'/>
            VibZ
        </div>
        <div className="flex gap-4 items-center">
            {isAdmin && (
                <Link to={"/admin"} className={cn(buttonVariants({ variant: "outline" }))}>
                    <LayoutDashboardIcon className="size-4 mr-2"/>
                    Admin Dashboard
                </Link>
            )}
            
            {user ? <><span className="text-sm text-zinc-300">{user.fullName}</span><Button variant="outline" onClick={signOut}>Sign out</Button></> : <SignInOAuthButtons />}
        </div>
    </div>
  )
};

export default Topbar
