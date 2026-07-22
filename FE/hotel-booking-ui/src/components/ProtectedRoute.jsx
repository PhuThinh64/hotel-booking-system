import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute = ({ allowedRoles }) => {
    const user = JSON.parse(localStorage.getItem('user'));

    
    if (!user) {
        return <Navigate to="/auth" replace />;
    }

    
    if (!allowedRoles.includes(user.role)) {
        return <Navigate to="/" replace />;
    }

    
    return <Outlet />;
};

export default ProtectedRoute;