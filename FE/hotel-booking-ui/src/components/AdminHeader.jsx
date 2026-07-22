import React, { useState, useEffect } from 'react'; 
import { useNavigate } from 'react-router-dom';
import { Layout, Avatar, Space, Button, Dropdown, Tag, theme } from 'antd';
import { UserOutlined, LogoutOutlined, MenuFoldOutlined, MenuUnfoldOutlined, LockOutlined } from '@ant-design/icons';
import ChangePasswordModal from '../pages/customer/ChangePasswordModal'; 

const { Header } = Layout;

const AdminHeader = ({ collapsed, onToggle, user, onLogout }) => {
    const { token: { colorBgContainer } } = theme.useToken();
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const navigate = useNavigate();
    const [currentUser, setCurrentUser] = useState(user);

    useEffect(() => {
        const handleUserUpdate = () => setCurrentUser(JSON.parse(localStorage.getItem('user')));
        window.addEventListener('user-updated', handleUserUpdate);
        return () => window.removeEventListener('user-updated', handleUserUpdate);
    }, []);

    const userMenu = {
        items: [
            { key: 'profile', label: 'Thông tin cá nhân', icon: <UserOutlined />, onClick: () => navigate('/admin/profile') },
            { key: 'change-pass', label: 'Đổi mật khẩu', icon: <LockOutlined />, onClick: () => setIsPasswordModalOpen(true) },
            { type: 'divider' },
            { key: 'logout', label: 'Đăng xuất', icon: <LogoutOutlined />, danger: true, onClick: onLogout }
        ]
    };

    return (
        <Header style={{ background: colorBgContainer, display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 20px' }}>
            <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={onToggle} />
            <Dropdown menu={userMenu} placement="bottomRight">
                <Space style={{ cursor: 'pointer' }}>
                    <Tag color={currentUser?.role === 'ROLE_ADMIN' ? 'red' : 'blue'}>
                        {currentUser?.role === 'ROLE_ADMIN' ? 'Admin' : 'Lễ tân'}
                    </Tag>
                    <Avatar icon={<UserOutlined />} />
                    <span>{currentUser?.fullName || 'User'}</span>
                </Space>
            </Dropdown>
            <ChangePasswordModal visible={isPasswordModalOpen} onClose={() => setIsPasswordModalOpen(false)} />
        </Header>
    );
};

export default AdminHeader;