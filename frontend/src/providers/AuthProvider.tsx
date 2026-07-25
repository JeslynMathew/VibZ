import React, { useEffect } from "react";
import { useAuthStore } from "@/stores/useAuthStore";
import { useChatStore } from "@/stores/useChatStore";

const AuthProvider = ({ children }: { children: React.ReactNode }) => {
	const { user, checkAdminStatus } = useAuthStore();
	const { startPresence, stopPresence } = useChatStore();

	useEffect(() => {
		if (user) {
			checkAdminStatus();
			startPresence(user._id);
		} else {
			stopPresence();
		}

		return () => stopPresence();
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [user?._id]);

	return <>{children}</>;
};

export default AuthProvider;
