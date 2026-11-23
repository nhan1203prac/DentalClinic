import { useEffect, useState } from 'react'
import { Alert, Button, Popconfirm, Table, Tag, Typography, message } from 'antd'
import dayjs from 'dayjs'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { bookingApi } from '../../api/bookingApi'
import { fetchMyAppointments } from '../../store/bookingSlice'
import { errorMessage } from '../../utils/formErrors'
import { shortTime } from '../../utils/week'

const STATUS = {
  da_dat: { color: 'blue', label: 'Đã đặt' },
  da_den: { color: 'cyan', label: 'Đã đến' },
  dang_kham: { color: 'orange', label: 'Đang khám' },
  hoan_thanh: { color: 'green', label: 'Hoàn thành' },
  vang_mat: { color: 'default', label: 'Vắng mặt' },
  da_huy: { color: 'red', label: 'Đã hủy' },
}

// Lịch khám của mọi người khám thuộc tài khoản; tự hủy lịch đang chờ trước giờ hẹn (BR-26)
export function MyAppointmentsPage() {
  const dispatch = useDispatch()
  const { items, status, error } = useSelector((state) => state.myAppointments)
  const [cancelling, setCancelling] = useState(null)

  useEffect(() => {
    dispatch(fetchMyAppointments())
  }, [dispatch])

  async function cancel(id) {
    setCancelling(id)
    try {
      await bookingApi.cancel(id)
      message.success('Đã hủy lịch hẹn')
    } catch (e) {
      message.error(errorMessage(e))
    } finally {
      setCancelling(null)
      // Tải lại cả khi lỗi: lịch có thể vừa quá giờ hoặc đã đổi trạng thái
      dispatch(fetchMyAppointments())
    }
  }

  const columns = [
    {
      title: 'Thời gian',
      render: (_, a) => (
        <>
          <Typography.Text strong>{shortTime(a.gioHen)}</Typography.Text>{' '}
          <Typography.Text>{dayjs(a.ngay).format('DD/MM/YYYY')}</Typography.Text>
        </>
      ),
    },
    { title: 'Người khám', dataIndex: 'tenBenhNhan' },
    { title: 'Bác sĩ', dataIndex: 'tenBacSi', responsive: ['sm'] },
    {
      title: 'Nội dung',
      responsive: ['md'],
      render: (_, a) => (
        <>
          {a.dichVu.map((name) => <Tag key={name}>{name}</Tag>)}
          {a.lyDoKham && <div><Typography.Text type="secondary">{a.lyDoKham}</Typography.Text></div>}
          {!a.lyDoKham && a.dichVu.length === 0 && '—'}
        </>
      ),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'trangThai',
      render: (value) => <Tag color={STATUS[value]?.color}>{STATUS[value]?.label ?? value}</Tag>,
    },
    {
      title: '',
      align: 'right',
      render: (_, a) =>
        a.coTheHuy && (
          <Popconfirm
            title="Hủy lịch hẹn này?"
            description="Khung giờ sẽ được trả lại cho người khác."
            okText="Hủy lịch"
            cancelText="Giữ lại"
            okButtonProps={{ danger: true }}
            onConfirm={() => cancel(a.maLichHen)}
          >
            <Button danger size="small" loading={cancelling === a.maLichHen}>Hủy lịch</Button>
          </Popconfirm>
        ),
    },
  ]

  return (
    <>
      <Typography.Title level={2}>Lịch khám của tôi</Typography.Title>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table
        rowKey="maLichHen"
        columns={columns}
        dataSource={items}
        loading={status === 'idle' || status === 'loading'}
        pagination={{ pageSize: 10, hideOnSinglePage: true }}
        locale={{ emptyText: <>Bạn chưa có lịch khám nào. <Link to="/app/dat-lich">Đặt lịch ngay</Link></> }}
      />
    </>
  )
}
