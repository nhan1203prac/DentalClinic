import { useEffect, useMemo, useState } from 'react'
import { Alert, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useDispatch, useSelector } from 'react-redux'
import { WeekNavigator } from '../../components/WeekNavigator'
import { fetchSchedule } from '../../store/scheduleSlice'
import { DAY_LABELS, mondayOf, shortTime, weekDates } from '../../utils/week'

// Bác sĩ xem lịch làm việc của chính mình (backend chỉ trả lịch của bác sĩ đang đăng nhập)
export function MySchedulePage() {
  const dispatch = useDispatch()
  const [weekStart, setWeekStart] = useState(mondayOf())
  const { items, status, error } = useSelector((state) => state.schedule)

  useEffect(() => {
    dispatch(fetchSchedule(weekStart))
  }, [dispatch, weekStart])

  // Luôn đủ 7 ngày, ngày chưa có lịch hiển thị "Không có lịch"
  const rows = useMemo(() => {
    const byDate = new Map(items.map((s) => [s.ngay, s]))
    return weekDates(weekStart).map((date, i) => ({ date, label: DAY_LABELS[i], item: byDate.get(date) ?? null }))
  }, [items, weekStart])

  const today = dayjs().format('YYYY-MM-DD')
  const columns = [
    {
      title: 'Ngày',
      dataIndex: 'label',
      render: (label, row) => (
        <>
          <Typography.Text strong={row.date === today}>{label}</Typography.Text>{' '}
          <Typography.Text type="secondary">{dayjs(row.date).format('DD/MM')}</Typography.Text>
          {row.date === today && <Tag color="blue" style={{ marginInlineStart: 8 }}>Hôm nay</Tag>}
        </>
      ),
    },
    {
      title: 'Giờ làm việc',
      render: (_, { item }) => (item && item.trangThai === 'lam_viec' ? `${shortTime(item.gioBatDau)} – ${shortTime(item.gioKetThuc)}` : '—'),
    },
    {
      title: 'Trạng thái',
      render: (_, { item }) => {
        if (!item) {
          return <Typography.Text type="secondary">Không có lịch</Typography.Text>
        }
        return item.trangThai === 'lam_viec' ? <Tag color="green">Làm việc</Tag> : <Tag color="red">Nghỉ</Tag>
      },
    },
    {
      title: 'Lịch hẹn',
      responsive: ['sm'],
      render: (_, { item }) => (item && item.soLichHen > 0 ? `${item.soLichHen} lịch hẹn` : '—'),
    },
    { title: 'Ghi chú', responsive: ['md'], render: (_, { item }) => item?.ghiChu ?? '—' },
  ]

  return (
    <>
      <Typography.Title level={2}>Lịch làm việc</Typography.Title>
      <div style={{ marginBottom: 16 }}>
        <WeekNavigator weekStart={weekStart} onChange={setWeekStart} />
      </div>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table rowKey="date" columns={columns} dataSource={rows} loading={status === 'idle' || status === 'loading'} pagination={false} />
    </>
  )
}
