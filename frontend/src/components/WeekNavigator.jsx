import { Button, DatePicker, Space, Typography } from 'antd'
import { LeftOutlined, RightOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { mondayOf, shiftWeek, weekEnd } from '../utils/week'

// Chọn tuần: onChange nhận yyyy-MM-dd của thứ Hai
export function WeekNavigator({ weekStart, onChange }) {
  return (
    <Space wrap>
      <Button icon={<LeftOutlined />} onClick={() => onChange(shiftWeek(weekStart, -1))} aria-label="Tuần trước" />
      <DatePicker
        picker="week"
        allowClear={false}
        value={dayjs(weekStart)}
        format={() => `${dayjs(weekStart).format('DD/MM/YYYY')} – ${dayjs(weekEnd(weekStart)).format('DD/MM/YYYY')}`}
        onChange={(date) => date && onChange(mondayOf(date))}
        style={{ width: 240 }}
      />
      <Button icon={<RightOutlined />} onClick={() => onChange(shiftWeek(weekStart, 1))} aria-label="Tuần sau" />
      <Button onClick={() => onChange(mondayOf())}>Tuần này</Button>
      {weekStart === mondayOf() && <Typography.Text type="secondary">(đang xem tuần hiện tại)</Typography.Text>}
    </Space>
  )
}
