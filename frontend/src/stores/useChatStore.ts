import { axiosInstance } from "@/lib/axios";
import { Message, User } from "@/types";
import { create } from "zustand";

const ONLINE_THRESHOLD_MS = 30_000; // matches the ~8s heartbeat interval below with room to spare
const PRESENCE_POLL_MS = 8_000;
const MESSAGES_POLL_MS = 3_000;

interface ChatStore {
	users: User[];
	isLoading: boolean;
	error: string | null;
	isConnected: boolean;
	onlineUsers: Set<string>;
	userActivities: Map<string, string>;
	messages: Message[];
	selectedUser: User | null;

	fetchUsers: () => Promise<void>;
	startPresence: (userId: string) => void;
	stopPresence: () => void;
	setActivity: (activity: string) => void;
	sendMessage: (receiverId: string, content: string) => Promise<void>;
	fetchMessages: (userId: string) => Promise<void>;
	startMessagePolling: (userId: string) => void;
	stopMessagePolling: () => void;
	setSelectedUser: (user: User | null) => void;
}

// Interval handles live outside of store state since they aren't UI data.
let presenceInterval: ReturnType<typeof setInterval> | null = null;
let messagesInterval: ReturnType<typeof setInterval> | null = null;
let currentActivity = "Idle";

export const useChatStore = create<ChatStore>((set, get) => ({
	users: [],
	isLoading: false,
	error: null,
	isConnected: false,
	onlineUsers: new Set(),
	userActivities: new Map(),
	messages: [],
	selectedUser: null,

	setSelectedUser: (user) => set({ selectedUser: user }),

	fetchUsers: async () => {
		set({ isLoading: true, error: null });
		try {
			const response = await axiosInstance.get("/users");
			const users = response.data as User[];
			const now = Date.now();
			const onlineUsers = new Set<string>();
			const userActivities = new Map<string, string>();
			users.forEach((u) => {
				if (u.lastActiveAt && now - new Date(u.lastActiveAt).getTime() < ONLINE_THRESHOLD_MS) {
					onlineUsers.add(u.clerkId);
				}
				userActivities.set(u.clerkId, u.activity ?? "Idle");
			});
			set({ users, onlineUsers, userActivities });
		} catch (error: any) {
			set({ error: error.response?.data?.message ?? "Unable to load users" });
		} finally {
			set({ isLoading: false });
		}
	},

	// Replaces the old (never-actually-wired-up) socket.io presence system. There's no
	// socket.io server on the Spring backend, so instead we send a lightweight heartbeat
	// and re-poll the users list to figure out who's online and what they're playing.
	startPresence: (_userId) => {
		if (get().isConnected) return;
		currentActivity = "Idle";

		const beat = async () => {
			try {
				await axiosInstance.post("/users/heartbeat", { activity: currentActivity });
			} catch {
				// ignore transient heartbeat failures
			}
			get().fetchUsers();
		};

		beat();
		presenceInterval = setInterval(beat, PRESENCE_POLL_MS);
		set({ isConnected: true });
	},

	stopPresence: () => {
		if (presenceInterval) clearInterval(presenceInterval);
		presenceInterval = null;
		set({ isConnected: false, onlineUsers: new Set(), userActivities: new Map() });
	},

	setActivity: (activity) => {
		currentActivity = activity;
		if (!axiosInstance.defaults.headers.common["X-User-Id"]) return;
		axiosInstance.post("/users/heartbeat", { activity }).catch(() => {});
	},

	sendMessage: async (receiverId, content) => {
		try {
			const response = await axiosInstance.post("/users/messages", { receiverId, content });
			set((state) => ({ messages: [...state.messages, response.data] }));
		} catch (error: any) {
			set({ error: error.response?.data?.message ?? "Unable to send message" });
		}
	},

	fetchMessages: async (userId: string) => {
		set({ isLoading: true, error: null });
		try {
			const response = await axiosInstance.get(`/users/messages/${userId}`);
			set({ messages: response.data });
		} catch (error: any) {
			set({ error: error.response?.data?.message ?? "Unable to load messages" });
		} finally {
			set({ isLoading: false });
		}
	},

	startMessagePolling: (userId) => {
		get().stopMessagePolling();
		messagesInterval = setInterval(() => get().fetchMessages(userId), MESSAGES_POLL_MS);
	},

	stopMessagePolling: () => {
		if (messagesInterval) clearInterval(messagesInterval);
		messagesInterval = null;
	},
}));
