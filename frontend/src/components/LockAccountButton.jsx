import { Button, Popconfirm, message } from 'antd'
import { adminApi } from '../api/adminApi'
import { errorMessage } from '../utils/formErrors'

// Khóa/mở khóa đăng nhập, không đổi trạng thái làm việc
export function LockAccountButton({ accountId, locked, onDone }) {
  async function toggle() {
    try {
      await adminApi.lockAccount(accountId, !locked)
      message.success(locked ? 'Đã mở khóa tài khoản' : 'Đã khóa tài khoản')
      onDone()
    } catch (e) {
      message.error(errorMessage(e))
    }
  }

  return (
    <Popconfirm title={locked ? 'Mở khóa tài khoản này?' : 'Khóa tài khoản này? Họ sẽ không đăng nhập được.'} onConfirm={toggle} okText="Đồng ý" cancelText="Hủy">
      <Button type="link" danger={!locked}>{locked ? 'Mở khóa' : 'Khóa'}</Button>
    </Popconfirm>
  )
}
