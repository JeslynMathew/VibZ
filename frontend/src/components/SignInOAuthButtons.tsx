import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from "@/components/ui/dialog";
import { useAuthStore } from "@/stores/useAuthStore";

const SignInOAuthButtons = () => {
	const [open, setOpen] = useState(false);
	const [mode, setMode] = useState<"login" | "register">("login");
	const [fullName, setFullName] = useState("");
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");

	const { login, register, isLoading, error } = useAuthStore();

	const resetForm = () => {
		setFullName("");
		setEmail("");
		setPassword("");
	};

	const handleOpenChange = (next: boolean) => {
		setOpen(next);
		if (!next) resetForm();
	};

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		const success = mode === "login" ? await login(email, password) : await register(fullName, email, password);
		if (success) {
			resetForm();
			setOpen(false);
		}
	};

	return (
		<Dialog open={open} onOpenChange={handleOpenChange}>
			<DialogTrigger asChild>
				<Button variant='outline'>Sign in</Button>
			</DialogTrigger>

			<DialogContent className='bg-zinc-900 border-zinc-700'>
				<DialogHeader>
					<DialogTitle>{mode === "login" ? "Sign in" : "Create account"}</DialogTitle>
					<DialogDescription>
						{mode === "login"
							? "Sign in with your email and password."
							: "Create an account to start listening and chatting."}
					</DialogDescription>
				</DialogHeader>

				<form className='space-y-4' onSubmit={handleSubmit}>
					{mode === "register" && (
						<Input
							placeholder='Full name'
							value={fullName}
							onChange={(e) => setFullName(e.target.value)}
							className='bg-zinc-800 border-zinc-700'
							required
						/>
					)}

					<Input
						type='email'
						placeholder='Email'
						value={email}
						onChange={(e) => setEmail(e.target.value)}
						className='bg-zinc-800 border-zinc-700'
						required
					/>

					<Input
						type='password'
						placeholder='Password'
						value={password}
						onChange={(e) => setPassword(e.target.value)}
						minLength={6}
						className='bg-zinc-800 border-zinc-700'
						required
					/>

					{error && <p className='text-sm text-red-400'>{error}</p>}

					<DialogFooter className='flex-col sm:flex-col items-stretch gap-2'>
						<Button type='submit' disabled={isLoading} className='w-full'>
							{isLoading ? "Please wait..." : mode === "login" ? "Sign in" : "Create account"}
						</Button>
						<button
							type='button'
							className='text-xs text-zinc-400 hover:text-zinc-200 underline text-center'
							onClick={() => setMode(mode === "login" ? "register" : "login")}
						>
							{mode === "login" ? "Need an account? Register" : "Already have an account? Sign in"}
						</button>
					</DialogFooter>
				</form>
			</DialogContent>
		</Dialog>
	);
};

export default SignInOAuthButtons;
