import { Button, Form, Input, Modal, Popconfirm, Radio, Table, Tag, Typography, TimePicker, message } from 'antd'
import dayjs from 'dayjs'
import { scheduleApi } from '../../api/scheduleApi'
import { EntityFormModal } from '../../components/EntityFormModal'
import { errorMessage } from '../../utils/formErrors'
import { shortTime } from '../../utils/week'

const TIME_FORMAT = 'HH:mm'
const DEFAULT_RANGE = ['08:00', '17:00']

const APPOINTMENT_STATUS = { da_dat: 'Đã đặt', da_den: 'Đã đến' }

// Lịch hẹn nằm ngoài buổi sau khi đổi: nhân viên cần liên hệ và hủy (BR-15)
function showAffectedAppointments(affected) {
  Modal.warning({
    title: `${affected.length} lịch hẹn bị ảnh hưởng`,
    width: 720,
    content: (
      <>
        <Typography.Paragraph>
          Hệ thống không tự hủy lịch hẹn. Hãy liên hệ bệnh nhân và nhờ nhân viên hủy các lịch dưới đây.
        </Typography.Paragraph>
        <Table
          size="small"
          rowKey="maLichHen"
          pagination={false}
          dataSource={affected}
          columns={[
            { title: 'Giờ', dataIndex: 'gioHen', render: shortTime, width: 70 },
            { title: 'Bệnh nhân', dataIndex: 'hoTenBenhNhan' },
            { title: 'Điện thoại', dataIndex: 'soDienThoai' },
            { title: 'Lý do khám', dataIndex: 'lyDoKham', render: (v) => v ?? '—' },
            { title: 'Trạng thái', dataIndex: 'trangThai', render: (v) => <Tag>{APPOINTMENT_STATUS[v] ?? v}</Tag> },
          ]}
        />
      </>
    ),
  })
}

// cell = { doctor, date (yyyy-MM-dd), schedule | null }. onSaved() tải lại lịch tuần.
export function ScheduleCellModal({ cell, weekStart, onClose, onSaved }) {
  const schedule = cell?.schedule
  const hasAppointments = (schedule?.soLichHen ?? 0) > 0

  const initialValues = {
    trangThai: schedule?.trangThai ?? 'lam_viec',
    gio: [
      dayjs(schedule ? shortTime(schedule.gioBatDau) : DEFAULT_RANGE[0], TIME_FORMAT),
      dayjs(schedule ? shortTime(schedule.gioKetThuc) : DEFAULT_RANGE[1], TIME_FORMAT),
    ],
    ghiChu: schedule?.ghiChu ?? '',
  }

  async function handleSubmit(values) {
    const [start, end] = values.gio
    const startTime = `${start.format(TIME_FORMAT)}:00`
    const endTime = `${end.format(TIME_FORMAT)}:00`
    if (hasAppointments) {
      // Buổi đã có lịch hẹn chỉ đổi được qua PATCH để biết lịch hẹn nào bị ảnh hưởng
      const result = await scheduleApi.update(schedule.maLich, {
        gioBatDau: startTime,
        gioKetThuc: endTime,
        trangThai: values.trangThai,
        ghiChu: values.ghiChu ?? '',
      })
      if (result.lichHenBiAnhHuong.length > 0) {
        showAffectedAppointments(result.lichHenBiAnhHuong)
      }
    } else {
      await scheduleApi.saveWeek(weekStart, [{
        maBacSi: cell.doctor.maBacSi,
        ngay: cell.date,
        gioBatDau: startTime,
        gioKetThuc: endTime,
        trangThai: values.trangThai,
        ghiChu: values.ghiChu || null,
      }])
    }
    message.success('Đã lưu lịch làm việc')
    onClose()
    onSaved()
  }

  async function handleDelete() {
    try {
      await scheduleApi.saveWeek(weekStart, [], [{ maBacSi: cell.doctor.maBacSi, ngay: cell.date }])
      message.success('Đã xóa buổi làm việc')
      onClose()
      onSaved()
    } catch (e) {
      message.error(errorMessage(e))
    }
  }

  return (
    <EntityFormModal
      title={cell ? `${cell.doctor.hoTen} – ${dayjs(cell.date).format('dddd DD/MM/YYYY')}` : ''}
      open={cell !== null}
      initialValues={initialValues}
      onCancel={onClose}
      onSubmit={handleSubmit}
    >
      {hasAppointments && (
        <Typography.Paragraph type="warning">
          Buổi này đã có {schedule.soLichHen} lịch hẹn. Nếu đổi giờ hoặc chuyển nghỉ, hệ thống sẽ liệt kê các lịch hẹn bị ảnh hưởng để bạn xử lý.
        </Typography.Paragraph>
      )}
      <Form.Item name="trangThai" label="Trạng thái">
        <Radio.Group optionType="button" buttonStyle="solid" options={[
          { value: 'lam_viec', label: 'Làm việc' },
          { value: 'nghi', label: 'Nghỉ' },
        ]} />
      </Form.Item>
      <Form.Item name="gio" label="Giờ làm việc (theo mốc 30 phút)" rules={[{ required: true, message: 'Chọn giờ bắt đầu và kết thúc' }]}>
        <TimePicker.RangePicker format={TIME_FORMAT} minuteStep={30} showSecond={false} needConfirm={false} style={{ width: '100%' }} />
      </Form.Item>
      <Form.Item name="ghiChu" label="Ghi chú" rules={[{ max: 255 }]}>
        <Input placeholder="Lý do nghỉ, ghi chú làm thêm..." />
      </Form.Item>
      {schedule && (
        <Popconfirm title="Xóa buổi làm việc này?" onConfirm={handleDelete} okText="Xóa" cancelText="Hủy" disabled={hasAppointments}>
          <Button danger disabled={hasAppointments}>Xóa buổi này</Button>
        </Popconfirm>
      )}
      {hasAppointments && (
        <Typography.Text type="secondary" style={{ marginLeft: 8 }}>
          Buổi đã có lịch hẹn không xóa được, hãy chuyển sang Nghỉ.
        </Typography.Text>
      )}
    </EntityFormModal>
  )
}
