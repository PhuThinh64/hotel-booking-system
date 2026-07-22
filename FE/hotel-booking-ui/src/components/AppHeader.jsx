import React, { useState, useEffect, useMemo } from 'react';
import { Layout, Button, Dropdown, Space, Avatar, message, Typography } from 'antd'; 
import { 
  UserOutlined, 
  HistoryOutlined, 
  LogoutOutlined, 
  LoginOutlined, 
  LockOutlined, 
  DownOutlined,
  UserAddOutlined 
} from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import ChangePasswordModal from '../pages/customer/ChangePasswordModal'; 

const { Header } = Layout;
const { Text } = Typography; 

const AppHeader = () => {
    const navigate = useNavigate();
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false); 
    const [currentUser, setCurrentUser] = useState(() => JSON.parse(localStorage.getItem('user')));

    useEffect(() => {
        const handleUserUpdate = () => setCurrentUser(JSON.parse(localStorage.getItem('user')));
        window.addEventListener('user-updated', handleUserUpdate);
        return () => window.removeEventListener('user-updated', handleUserUpdate);
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setCurrentUser(null);
        message.success('Đăng xuất thành công!');
        navigate('/auth');
    };

    const userMenu = useMemo(() => {
        if (!currentUser) return [];

        const role = currentUser.role;
        const isAdminOrStaff = ['ADMIN', 'ROLE_ADMIN', 'RECEPTIONIST', 'ROLE_RECEPTIONIST'].includes(role);
        const profilePath = isAdminOrStaff ? '/admin/profile' : '/profile'; 

        return [
            { 
                key: 'profile', 
                label: <Link to={profilePath} style={{ color: '#262626' }}>Thông tin cá nhân</Link>, 
                icon: <UserOutlined style={{ fontSize: '15px', color: '#8c8c8c' }} />, 
                style: { padding: '10px 16px', borderRadius: '8px' }
            },
            { 
                key: 'change-password', 
                label: 'Đổi mật khẩu', 
                icon: <LockOutlined style={{ fontSize: '15px', color: '#8c8c8c' }} />, 
                onClick: () => setIsPasswordModalOpen(true),
                style: { padding: '10px 16px', borderRadius: '8px' }
            },
            { 
                type: 'divider', 
                style: { margin: '6px 0' } 
            },
            { 
                key: 'logout', 
                label: 'Đăng xuất', 
                icon: <LogoutOutlined style={{ fontSize: '15px' }} />, 
                danger: true, 
                onClick: handleLogout,
                style: { padding: '10px 16px', borderRadius: '8px', fontWeight: '500' }
            }
        ];
    }, [currentUser]);

    const isUser = currentUser?.role === 'USER' || currentUser?.role === 'ROLE_USER';

    const avatarLetter = useMemo(() => {
        if (!currentUser) return '';
        const name = currentUser.fullName || currentUser.username || '';
        return name.charAt(0).toUpperCase();
    }, [currentUser]);

    return (
        <Header style={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center', 
            background: '#fff', 
            padding: '0 50px',
            boxShadow: '0 2px 12px rgba(0, 0, 0, 0.06)', 
            position: 'relative',
            zIndex: 1000
        }}>
            <style>{`
                .order-link-premium {
                    font-weight: 500 !important;
                    display: inline-flex !important;
                    align-items: center !important;
                    gap: 6px !important;
                    color: #1890ff !important;
                    transition: opacity 0.2s ease;
                }
                .order-link-premium:hover {
                    opacity: 0.8;
                }
                .user-profile-trigger {
                    cursor: pointer; 
                    padding: 4px 10px; 
                    border-radius: 8px; 
                    transition: all 0.25s ease;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    line-height: normal !important;
                }
                .user-profile-trigger:hover {
                    background-color: #f5f5f5;
                }
            `}</style>

            <Link 
                to="/" 
                style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    gap: '12px',
                    textDecoration: 'none'
                }}
            >
                <div style={{
                    background: 'linear-gradient(135deg, #1890ff 0%, #0050b3 100%)',
                    width: '36px',
                    height: '36px',
                    borderRadius: '10px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    boxShadow: '0 4px 12px rgba(24, 144, 255, 0.3)'
                }}>
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 22C12 22 6 16 6 11C6 7.5 9 5 12 2C15 5 18 7.5 18 11C18 16 12 22 12 22Z" fill="white"/>
                        <path d="M12 22C14.5 19.5 21 18 21 14C21 11.5 18.5 10.5 16 12C14 13.2 13 15.5 12 17.5C11 15.5 10 13.2 8 12C5.5 10.5 3 11.5 3 14C3 18 9.5 19.5 12 22Z" fill="white" fillOpacity="0.85"/>
                    </svg>
                </div>

                <span style={{ 
                    fontWeight: '800', 
                    fontSize: '22px', 
                    letterSpacing: '1px',
                    background: 'linear-gradient(90deg, #0050b3 0%, #1890ff 100%)', 
                    WebkitBackgroundClip: 'text', 
                    WebkitTextFillColor: 'transparent',
                    textTransform: 'uppercase'
                }}>
                    Lotus Hotel
                </span>
            </Link>

            <div className="auth-section" style={{ display: 'flex', alignItems: 'center' }}>
                {currentUser ? (
                    <Space size="large" align="center">
                        {isUser && (
                            <Link to="/my-bookings" className="order-link-premium">
                                <HistoryOutlined /> Đơn hàng
                            </Link>
                        )}
                        
                        <Dropdown 
                            menu={{ 
                                items: userMenu,
                                style: {
                                    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.08)', 
                                    borderRadius: '10px',
                                    padding: '6px',
                                    border: '1px solid #f0f0f0'
                                }
                            }} 
                            trigger={['hover']} 
                            placement="bottomRight"
                        >
                            <div className="user-profile-trigger">
                                <Avatar 
                                    size={36} 
                                    style={{ 
                                        backgroundColor: '#e6f7ff', 
                                        color: '#1890ff',
                                        fontWeight: '600',
                                        boxShadow: '0 2px 6px rgba(24, 144, 255, 0.12)'
                                    }}
                                >
                                    {avatarLetter}
                                </Avatar>
                                
                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', lineHeight: '1.2' }}>
                                    <Text strong style={{ color: '#262626', fontSize: '14px' }}>
                                        {currentUser.fullName || currentUser.username}
                                    </Text>
                                    <Text type="secondary" style={{ fontSize: '11px' }}>
                                        @{currentUser.username}
                                    </Text>
                                </div>
                                <DownOutlined style={{ fontSize: '10px', color: '#bfbfbf', marginLeft: '2px' }} />
                            </div>
                        </Dropdown>
                    </Space>
                ) : (
                    <Space size="middle" align="center">
                        <Button 
                            size="large"
                            icon={<UserAddOutlined />}
                            style={{ 
                                borderRadius: '8px', 
                                fontWeight: '500',
                                color: '#434343',
                                borderColor: '#d9d9d9',
                                padding: '0 20px',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '6px',
                                boxShadow: '0 2px 0 rgba(0, 0, 0, 0.015)'
                            }}
                            onClick={() => navigate('/auth', { state: { tab: '2' } })}
                        >
                            Đăng ký
                        </Button>

                        <Button 
                            type="primary" 
                            size="large"
                            icon={<LoginOutlined />}
                            style={{ 
                                borderRadius: '8px', 
                                fontWeight: '600',
                                background: 'linear-gradient(135deg, #1890ff 0%, #0050b3 100%)', 
                                border: 'none',
                                padding: '0 24px',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '6px',
                                boxShadow: '0 4px 14px rgba(24, 144, 255, 0.35)', 
                            }}
                            onClick={() => navigate('/auth', { state: { tab: '1' } })}
                        >
                            Đăng nhập
                        </Button>
                    </Space>
                )}
            </div>
            <ChangePasswordModal visible={isPasswordModalOpen} onClose={() => setIsPasswordModalOpen(false)} />
        </Header>
    );
};

export default AppHeader;