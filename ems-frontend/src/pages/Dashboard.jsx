import { useAuth } from '../context/AuthContext';
import AdminDashboard from './AdminDashboard';
import ManagerDashboard from './ManagerDashboard';
import EmployeeDashboard from './EmployeeDashboard';

export default function Dashboard() {
  const { user } = useAuth();

  if (!user) return null;

  if (user.role === 'ROLE_ADMIN') {
    return <AdminDashboard />;
  } else if (user.role === 'ROLE_MANAGER') {
    return <ManagerDashboard />;
  } else {
    return <EmployeeDashboard />;
  }
}
