import LoginForm from "./LoginForm";
import SignupForm from "./SignupForm";
import "../styles/AuthModal.css";

function AuthModal({ mode, close, switchMode, onAuthSuccess }) {
    return (
        <div className="auth-overlay" onClick={close}>
            <div className="auth-modal" onClick={(e) => e.stopPropagation()}>
                {mode === "login" ? (
                    <>
                        <LoginForm
                            onSuccess={() => {
                                onAuthSuccess && onAuthSuccess(); // ✅ modal kapanır
                            }}
                        />
                        <p className="auth-switch">
                            No account?
                            <span onClick={() => switchMode("signup")}> Sign up</span>
                        </p>
                    </>
                ) : (
                    <>
                        <SignupForm
                            onSuccess={() => {
                                onAuthSuccess && onAuthSuccess(); // ✅ modal kapanır
                            }}
                            goLogin={() => switchMode("login")}
                        />
                        <p className="auth-switch">
                            Already have an account?
                            <span onClick={() => switchMode("login")}> Log in</span>
                        </p>
                    </>
                )}
            </div>
        </div>
    );
}

export default AuthModal;
