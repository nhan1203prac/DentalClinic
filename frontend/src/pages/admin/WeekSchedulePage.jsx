import { useEffect, useMemo, useState } from 'react'
import { Alert, Button, Popconfirm, Space, Table, Tag, Typography, message } from 'antd'
import { CopyOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useDispatch, useSelector } from 'react-redux'
import { scheduleApi } from '../../api/scheduleApi'
import { WeekNavigator } from '../../components/WeekNavigator'
import { fetchDoctorsAdmin } from '../../store/doctorAdminSlice'
import { fetchSchedule } from '../../store/scheduleSlice'
import { errorMessage } from '../../utils/formErrors'
import { DAY_LABELS, mondayOf, shortTime, weekDates } from '../../utils/week'
import { ScheduleCellModal } from './ScheduleCellModal'

// Sắp lịch tuần cho chủ phòng khám: hàng là bác sĩ, cột là 7 ngày; bấm vào ô để thêm, sửa, xóa
export function WeekSchedulePage() {
  const dispatch = useDispatch()
  const [weekStart, setWeekStart] = useState(mondayOf())
  const [editing, setEditing] = useState(null)
  const [copying, setCopying] = useState(false)
  const schedule = useSelector((state) => state.schedule)
  const doctorsState = useSelector((state) => state.doctorAdmin)

  useEffect(() => {
    dispatch(fetchSchedule(weekStart))
  }, [dispatch, weekStart])

  useEffect(() => {
    dispatch(fetchDoctorsAdmin())
  }, [dispatch])

  const reload = () => dispatch(fetchSchedule(weekStart))
  const dates = useMemo(() => weekDates(weekStart), [weekStart])
  const doctors = useMemo(() => doctorsState.items.filter((d) => d.trangThaiLamViec === 'dang_lam'), [doctorsState.items])
  const byCell = useMemo(
    () => new Map(schedule.items.map((s) => [`${s.maBacSi}|${s.ngay}`, s])),
    [schedule.items],
  )

  async function copyPreviousWeek() {
    setCopying(true)
    try {
      const result = await scheduleApi.copyPreviousWeek(weekStart)
      message.success(`Đã chép ${result.daChep} buổi, bỏ qua ${result.boQua} buổi đã có lịch hoặc bác sĩ đã nghỉ việc`)
      reload()
    } catch (e) {
      message.error(errorMessage(e))
    } finally {
      setCopying(false)
    }
  }

  function renderCell(doctor, date) {
    const item = byCell.get(`${doctor.maBacSi}|${date}`)
    const open = () => setEditing({ doctor, date, schedule: item ?? null })
    if (!item) {
      return <Button type="dashed" block icon={<PlusOutlined />} onClick={open} aria-label="Thêm buổi làm việc" />
    }
    const off = item.trangThai === 'nghi'
    return (
      <Button block danger={off} type={off ? 'default' : 'primary'} ghost={!off} onClick={open} style={{ height: 'auto', padding: '4px 6px' }}>
        <div>{off ? 'Nghỉ' : `${shortTime(item.gioBatDau)}–${shortTime(item.gioKetThuc)}`}</div>
        {item.soLichHen > 0 && <Tag color="gold" style={{ marginInlineEnd: 0 }}>{item.soLichHen} lịch hẹn</Tag>}
      </Button>
    )
  }

  const columns = [
    { title: 'Bác sĩ', dataIndex: 'hoTen', fixed: 'left', width: 160 },
    ...dates.map((date, i) => ({
      title: (
        <div style={{ textAlign: 'center' }}>
          <div>{DAY_LABELS[i]}</div>
          <Typography.Text type={date === dayjs().format('YYYY-MM-DD') ? 'danger' : 'secondary'}>{dayjs(date).format('DD/MM')}</Typography.Text>
        </div>
      ),
      key: date,
      width: 120,
      render: (_, doctor) => renderCell(doctor, date),
    })),
  ]

  const error = schedule.error ?? doctorsState.error
  return (
    <>
      <Typography.Title level={2}>Sắp lịch tuần</Typography.Title>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <WeekNavigator weekStart={weekStart} onChange={setWeekStart} />
        <Popconfirm title="Chép lịch tuần trước sang tuần này?" description="Chỉ chép buổi làm việc, ngày đã có lịch được giữ nguyên." onConfirm={copyPreviousWeek} okText="Chép" cancelText="Hủy">
          <Button icon={<CopyOutlined />} loading={copying}>Sao chép tuần trước</Button>
        </Popconfirm>
      </Space>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Table
        rowKey="maBacSi"
        columns={columns}
        dataSource={doctors}
        loading={schedule.status === 'idle' || schedule.status === 'loading' || doctorsState.status === 'loading'}
        pagination={false}
        scroll={{ x: 1000 }}
        locale={{ emptyText: 'Chưa có bác sĩ đang làm việc' }}
      />
      <ScheduleCellModal cell={editing} weekStart={weekStart} onClose={() => setEditing(null)} onSaved={reload} />
    </>
  )
}
