import { useEffect } from 'react'
import { Alert, Avatar, Button, Card, DatePicker, Empty, Skeleton, Space, Typography } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useDispatch, useSelector } from 'react-redux'
import { fetchFreeSlots } from '../../store/bookingSlice'
import { shortTime } from '../../utils/week'
import { MAX_BOOKING_DAYS } from './bookingConstants'

const DATE_FORMAT = 'YYYY-MM-DD'

// Bước 3: chọn ngày, thấy thẻ từng bác sĩ kèm các nút giờ (BR-20, BR-25)
export function SlotStep({ draft, onChange, onBack, onNext }) {
  const dispatch = useDispatch()
  const { items, status, error } = useSelector((state) => state.freeSlots)
  const date = draft.date

  useEffect(() => {
    dispatch(fetchFreeSlots(date))
  }, [dispatch, date])

  const today = dayjs().startOf('day')
  const outOfRange = (d) => d.isBefore(today) || d.isAfter(today.add(MAX_BOOKING_DAYS - 1, 'day'))
  const loading = status === 'idle' || status === 'loading'

  function pick(doctor, slot) {
    onChange({ scheduleId: doctor.maLich, doctorId: doctor.maBacSi, doctorName: doctor.hoTen, time: slot.gio })
  }

  return (
    <>
      <Space wrap align="center" style={{ marginBottom: 16 }}>
        <Typography.Text>Ngày khám:</Typography.Text>
        <DatePicker
          allowClear={false}
          format="DD/MM/YYYY"
          value={dayjs(date)}
          disabledDate={outOfRange}
          // Đổi ngày thì bỏ giờ đã chọn của ngày cũ
          onChange={(d) => d && onChange({ date: d.format(DATE_FORMAT), scheduleId: null, doctorId: null, doctorName: null, time: null })}
        />
        <Typography.Text type="secondary">Đặt được trong {MAX_BOOKING_DAYS} ngày tới</Typography.Text>
      </Space>

      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      {loading ? (
        <Skeleton active />
      ) : items.length === 0 ? (
        <Empty description="Không có bác sĩ nào còn khung trống trong ngày này, hãy chọn ngày khác" />
      ) : (
        <Space direction="vertical" style={{ width: '100%' }}>
          {items.map((doctor) => (
            <Card key={doctor.maLich} size="small">
              <Space style={{ marginBottom: 12 }}>
                <Avatar icon={<UserOutlined />} />
                <Typography.Text strong>{doctor.hoTen}</Typography.Text>
              </Space>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                {doctor.khung.map((slot) => (
                  <Button
                    key={slot.gio}
                    disabled={!slot.trong}
                    type={draft.scheduleId === doctor.maLich && draft.time === slot.gio ? 'primary' : 'default'}
                    onClick={() => pick(doctor, slot)}
                  >
                    {shortTime(slot.gio)}
                  </Button>
                ))}
              </div>
            </Card>
          ))}
        </Space>
      )}

      <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Button onClick={onBack}>Quay lại</Button>
        <Button type="primary" disabled={!draft.time} onClick={onNext}>Tiếp tục</Button>
      </div>
    </>
  )
}
