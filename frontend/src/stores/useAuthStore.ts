import { axiosInstance } from "@/lib/axios";
import { User } from "@/types";
import { create } from "zustand";

const savedUser = localStorage.getItem("vibz-user");
const initialUser = savedUser ? (JSON.parse(savedUser) as User) : null;
if (initialUser) axiosInstance.defaults.headers.common["X-User-Id"] = initialUser._id;

interface AuthStore {
	user: User | null;
	isAdmin: boolean;
	isLoading: boolean;
	error: string | null;
	register: (fullName: string, email: string, password: string) => Promise<boolean>;
	login: (email: string, password: string) => Promise<boolean>;
	signOut: () => void;
	checkAdminStatus: () => Promise<void>;
	reset: () => void;
}

const applySession = (user: User, set: (partial: Partial<AuthStore>) => void) => {
	axiosInstance.defaults.headers.common["X-User-Id"] = user._id;
	localStorage.setItem("vibz-user", JSON.stringify(user));
	set({ user, isAdmin: Boolean((user as User & { admin?: boolean }).admin) });
};

export const useAuthStore = create<AuthStore>((set, get) => ({
	user: initialUser,
	isAdmin: false,
	isLoading: false,
	error: null,

	register: async (fullName, email, password) => {
		set({ isLoading: true, error: null });
		try {
			const response = await axiosInstance.post("/auth/register", { fullName, email, password });
			applySession(response.data as User, set);
			return true;
		} catch (error: any) {
			set({ error: error.response?.data?.message ?? "Unable to create account" });
			return false;
		} finally {
			set({ isLoading: false });
		}
	},

	login: async (email, password) => {
		set({ isLoading: true, error: null });
		try {
			const response = await axiosInstance.post("/auth/login", { email, password });
			applySession(response.data as User, set);
			return true;
		} catch (error: any) {
			set({ error: error.response?.data?.message ?? "Invalid email or password" });
			return false;
		} finally {
			set({ isLoading: false });
		}
	},

	signOut: () => {
		delete axiosInstance.defaults.headers.common["X-User-Id"];
		localStorage.removeItem("vibz-user");
		set({ user: null, isAdmin: false });
	},

	checkAdminStatus: async () => {
		if (!get().user) return;
		try {
			const response = await axiosInstance.get("/admin/check");
			set({ isAdmin: response.data.admin });
		} catch {
			set({ isAdmin: false });
		}
	},

	reset: () => set({ isAdmin: false, isLoading: false, error: null }),
}));
