import {
    LayoutDashboard,
    User,
    LibraryBig,
    Book,
    Settings,
    PanelLeftClose,
    PanelLeftOpen,
    GraduationCap,
    Files

} from "lucide-react";

function Sidebar({ collapsed, onToggle, activePage, onNavigate }) {
    return (
        <aside className={`sidebar${collapsed ? " is-collapsed" : ""}`} aria-label="Main navigation">
            {/* Brand */}
            <a
                className="sidebar-brand"
                href="#home"
                aria-label="Campus home"
            >
                <span className="brand-mark">E</span>

                <span className="brand-copy">
                    <strong>Educaze</strong>
                    <small>ADMIN PORTAL</small>
                </span>
            </a>

            <button
                className="sidebar-toggle"
                type="button"
                onClick={onToggle}
                aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
                aria-expanded={!collapsed}
                title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
            >
                {collapsed ? <PanelLeftOpen size={17} /> : <PanelLeftClose size={17} />}
            </button>

            {/* Navigation */}
            <div className="sidebar-section-label">
                WORKSPACE
            </div>

            <nav className="sidebar-nav">
                <a
                    className={`sidebar-link${activePage === "dashboard" ? " active" : ""}`}
                    href="#dashboard"
                    aria-current={activePage === "dashboard" ? "page" : undefined}
                    onClick={(event) => {
                        event.preventDefault();
                        onNavigate("dashboard");
                    }}
                >
                    <span className="nav-icon" aria-hidden="true">
                        <LayoutDashboard />
                    </span>
                    <span>Dashboard</span>
                </a>

                <a
                    className="sidebar-link"
                    href="#dashboard"
                >
                    <span className="nav-icon" aria-hidden="true">
                        <GraduationCap/>
                    </span>
                    <span>Teachers</span>
                </a>

                <a
                    className={`sidebar-link${activePage === "students" ? " active" : ""}`}
                    href="#students"
                    aria-current={activePage === "students" ? "page" : undefined}
                    onClick={(event) => {
                        event.preventDefault();
                        onNavigate("students");
                    }}
                >
                    <span className="nav-icon" aria-hidden="true">
                        <User />
                    </span>
                    <span>Students</span>
                </a>

                <a
                    className={`sidebar-link${activePage === "departments" ? " active" : ""}`}
                    href="#departments"
                    aria-current={activePage === "departments" ? "page" : undefined}
                    onClick={(event) => {
                        event.preventDefault();
                        onNavigate("departments");
                    }}
                >
                    <span className="nav-icon" aria-hidden="true">
                        <LibraryBig/>
                    </span>
                    <span>Departments</span>
                </a>

                <a
                    className={`sidebar-link${activePage === "classes" ? " active" : ""}`}
                    href="#classes"
                    aria-current={activePage === "classes" ? "page" : undefined}
                    onClick={(event) => {
                        event.preventDefault();
                        onNavigate("classes");
                    }}
                >
                    <span className="nav-icon" aria-hidden="true">
                        <Book />
                    </span>
                    <span>Classes</span>
                </a>

                <a
                    className={`sidebar-link${activePage === "documents" ? " active" : ""}`}
                    href="#documents"
                    aria-current={activePage === "documents" ? "page" : undefined}
                    onClick={(event) => {
                        event.preventDefault();
                        onNavigate("documents");
                    }}
                >
                    <span className="nav-icon" aria-hidden="true"><Files /></span>
                    <span>Documents</span>
                </a>

            </nav>

            {/* Bottom section */}
            <div className="sidebar-bottom">
                <div className="sidebar-divider" />

                <a
                    className="sidebar-link"
                    href="#settings"
                >
                    <span className="nav-icon" aria-hidden="true">
                        <Settings />
                    </span>
                    <span>Settings</span>
                </a>

                {/* Profile */}
                <div className="profile-card">
                    <div className="profile-avatar">
                        AD
                    </div>

                    <div className="profile-copy">
                        <strong>Administrator</strong>
                        <small>School manager</small>
                    </div>

                    <span
                        className="profile-menu"
                        aria-hidden="true"
                    >
                        ···
                    </span>
                </div>
            </div>
        </aside>
    );
}

export default Sidebar;
