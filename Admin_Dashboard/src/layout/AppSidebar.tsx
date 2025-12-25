import { useCallback, useEffect, useRef, useState } from "react";
import { Link, useLocation } from "react-router";
import { useTranslation } from "react-i18next";
import { GridIcon, TaskIcon, PlugInIcon, HorizontaLDots, ChevronDownIcon, ListIcon } from "../icons";
import { useSidebar } from "../context/SidebarContext";
import SidebarWidget from "./SidebarWidget";

type NavItem = {
  name: string;
  icon: React.ReactNode;
  path?: string;
  subItems?: { name: string; path: string }[];
};

const AppSidebar: React.FC = () => {
  const { isExpanded, isMobileOpen, isHovered, setIsHovered } = useSidebar();
  const location = useLocation();
  const { t } = useTranslation();

  const navItems: NavItem[] = [
    { icon: <GridIcon />, name: t('navigation.dashboard'), path: "/" },
    {
      icon: <TaskIcon />,
      name: "Quản lý",
      subItems: [
        { name: "Người dùng", path: "/users" },
        { name: "Món ăn", path: "/foods" },
        { name: "Bài tập", path: "/exercises" },
      ],
    },
    {
      icon: <ListIcon />,
      name: "Danh mục",
      subItems: [
        { name: "Danh mục món ăn", path: "/food-categories" },
        { name: "Danh mục bài tập", path: "/exercise-categories" },
      ],
    },
    { icon: <PlugInIcon />, name: "Thông báo", path: "/notifications" },
  ];

  const [openSubmenu, setOpenSubmenu] = useState<number | null>(null);
  const [subMenuHeight, setSubMenuHeight] = useState<Record<string, number>>({});
  const subMenuRefs = useRef<Record<string, HTMLDivElement | null>>({});

  const isActive = useCallback((path: string) => location.pathname === path, [location.pathname]);

  useEffect(() => {
    navItems.forEach((nav, index) => {
      if (nav.subItems?.some((sub) => isActive(sub.path))) {
        setOpenSubmenu(index);
      }
    });
  }, [location]);

  useEffect(() => {
    if (openSubmenu !== null && subMenuRefs.current[openSubmenu]) {
      setSubMenuHeight((prev) => ({ ...prev, [openSubmenu]: subMenuRefs.current[openSubmenu]?.scrollHeight || 0 }));
    }
  }, [openSubmenu]);

  const handleSubmenuToggle = (index: number) => {
    setOpenSubmenu((prev) => (prev === index ? null : index));
  };

  return (
    <aside
      className={`fixed mt-16 flex flex-col lg:mt-0 top-0 px-5 left-0 bg-white dark:bg-gray-900 dark:border-gray-800 text-gray-900 h-screen transition-all duration-300 ease-in-out z-50 border-r border-gray-200 
        ${isExpanded || isMobileOpen ? "w-[290px]" : isHovered ? "w-[290px]" : "w-[90px]"}
        ${isMobileOpen ? "translate-x-0" : "-translate-x-full"} lg:translate-x-0`}
      onMouseEnter={() => !isExpanded && setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div className={`py-8 flex ${!isExpanded && !isHovered ? "lg:justify-center" : "justify-start"}`}>
        <Link to="/">
          {isExpanded || isHovered || isMobileOpen ? (
            <span className="text-xl font-bold text-brand-500">HealthCare Admin</span>
          ) : (
            <span className="text-xl font-bold text-brand-500">HC</span>
          )}
        </Link>
      </div>
      <div className="flex flex-col overflow-y-auto duration-300 ease-linear no-scrollbar">
        <nav className="mb-6">
          <div className="flex flex-col gap-4">
            <h2 className={`mb-4 text-xs uppercase flex leading-[20px] text-gray-400 ${!isExpanded && !isHovered ? "lg:justify-center" : "justify-start"}`}>
              {isExpanded || isHovered || isMobileOpen ? "Menu" : <HorizontaLDots className="size-6" />}
            </h2>
            <ul className="flex flex-col gap-2">
              {navItems.map((nav, index) => (
                <li key={index}>
                  {nav.subItems ? (
                    <>
                      <button
                        onClick={() => handleSubmenuToggle(index)}
                        className={`menu-item group w-full ${openSubmenu === index ? "menu-item-active" : "menu-item-inactive"} cursor-pointer ${!isExpanded && !isHovered ? "lg:justify-center" : "lg:justify-start"}`}
                      >
                        <span className={`menu-item-icon-size ${openSubmenu === index ? "menu-item-icon-active" : "menu-item-icon-inactive"}`}>{nav.icon}</span>
                        {(isExpanded || isHovered || isMobileOpen) && <span className="menu-item-text">{nav.name}</span>}
                        {(isExpanded || isHovered || isMobileOpen) && (
                          <ChevronDownIcon className={`ml-auto w-5 h-5 transition-transform duration-200 ${openSubmenu === index ? "rotate-180 text-brand-500" : ""}`} />
                        )}
                      </button>
                      {(isExpanded || isHovered || isMobileOpen) && (
                        <div
                          ref={(el) => { subMenuRefs.current[index] = el; }}
                          className="overflow-hidden transition-all duration-300"
                          style={{ height: openSubmenu === index ? `${subMenuHeight[index]}px` : "0px" }}
                        >
                          <ul className="mt-2 space-y-1 ml-9">
                            {nav.subItems.map((subItem) => (
                              <li key={subItem.path}>
                                <Link to={subItem.path} className={`menu-dropdown-item ${isActive(subItem.path) ? "menu-dropdown-item-active" : "menu-dropdown-item-inactive"}`}>
                                  {subItem.name}
                                </Link>
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}
                    </>
                  ) : (
                    nav.path && (
                      <Link to={nav.path} className={`menu-item group ${isActive(nav.path) ? "menu-item-active" : "menu-item-inactive"}`}>
                        <span className={`menu-item-icon-size ${isActive(nav.path) ? "menu-item-icon-active" : "menu-item-icon-inactive"}`}>{nav.icon}</span>
                        {(isExpanded || isHovered || isMobileOpen) && <span className="menu-item-text">{nav.name}</span>}
                      </Link>
                    )
                  )}
                </li>
              ))}
            </ul>
          </div>
        </nav>
        {(isExpanded || isHovered || isMobileOpen) && <SidebarWidget />}
      </div>
    </aside>
  );
};

export default AppSidebar;
