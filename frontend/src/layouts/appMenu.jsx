import {
  BellOutlined,
  CalendarOutlined,
  ContactsOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  MedicineBoxOutlined,
  ProfileOutlined,
  RobotOutlined,
  ScheduleOutlined,
  SearchOutlined,
  StopOutlined,
  TeamOutlined,
  UnorderedListOutlined,
  UserOutlined,
} from '@ant-design/icons'

export const ROLE_LABELS = {
  benh_nhan: 'Bệnh nhân',
  nhan_vien: 'Nhân viên',
  bac_si: 'Bác sĩ',
  chu_phong_kham: 'Chủ phòng khám',
}

// Menu và route của khu /app đều sinh từ danh sách này (theo màn hình trong docs/requirements.md mục 6)
export const APP_MENU = [
  { path: '/app/dat-lich', label: 'Đặt lịch', icon: <CalendarOutlined />, roles: ['benh_nhan'] },
  { path: '/app/lich-kham', label: 'Lịch khám của tôi', icon: <UnorderedListOutlined />, roles: ['benh_nhan'] },
  { path: '/app/ho-so', label: 'Hồ sơ khám bệnh', icon: <ProfileOutlined />, roles: ['benh_nhan'] },
  { path: '/app/thong-bao', label: 'Thông báo', icon: <BellOutlined />, roles: ['benh_nhan'] },
  { path: '/app/tai-khoan', label: 'Thông tin tài khoản', icon: <UserOutlined />, roles: ['benh_nhan'] },

  { path: '/app/tiep-nhan', label: 'Tiếp nhận hôm nay', icon: <ContactsOutlined />, roles: ['nhan_vien'] },
  { path: '/app/benh-nhan', label: 'Tra cứu bệnh nhân', icon: <SearchOutlined />, roles: ['nhan_vien'] },
  { path: '/app/huy-lich', label: 'Hủy lịch do bác sĩ nghỉ', icon: <StopOutlined />, roles: ['nhan_vien'] },

  { path: '/app/hang-cho', label: 'Hàng chờ hôm nay', icon: <UnorderedListOutlined />, roles: ['bac_si'] },
  { path: '/app/lich-lam-viec', label: 'Lịch làm việc', icon: <ScheduleOutlined />, roles: ['bac_si'] },

  { path: '/app/lich-tuan', label: 'Sắp lịch tuần', icon: <CalendarOutlined />, roles: ['chu_phong_kham'] },
  { path: '/app/nhan-vien', label: 'Nhân viên', icon: <TeamOutlined />, roles: ['chu_phong_kham'] },
  { path: '/app/bac-si', label: 'Bác sĩ', icon: <MedicineBoxOutlined />, roles: ['chu_phong_kham'] },

  { path: '/app/dich-vu', label: 'Dịch vụ', icon: <FileTextOutlined />, roles: ['nhan_vien', 'chu_phong_kham'] },
  { path: '/app/tai-lieu', label: 'Tài liệu', icon: <FolderOpenOutlined />, roles: ['nhan_vien', 'chu_phong_kham'] },
  { path: '/app/tro-ly', label: 'Trợ lý', icon: <RobotOutlined />, roles: ['nhan_vien', 'bac_si', 'chu_phong_kham'] },
]

export function menuForRole(role) {
  return APP_MENU.filter((item) => item.roles.includes(role))
}
