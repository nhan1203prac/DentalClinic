import { Alert, Button, Descriptions, Tag } from 'antd'
import dayjs from 'dayjs'
import { useSelector } from 'react-redux'
import { shortTime } from '../../utils/week'
import { historyLabel } from './bookingConstants'

// Bước 4: xem lại toàn bộ rồi xác nhận đặt
export function ConfirmStep({ draft, patient, submitting, error, onBack, onSubmit }) {
  const services = useSelector((state) => state.services.items)
  const patientName = patient ? patient.hoTen : draft.newPatient.hoTen
  const chosenServices = services.filter((s) => draft.serviceIds.includes(s.maDichVu))

  return (
    <>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Descriptions bordered size="small" column={1}>
        <Descriptions.Item label="Người khám">
          {patientName}
          {!patient && <Tag color="blue" style={{ marginInlineStart: 8 }}>Hồ sơ mới</Tag>}
        </Descriptions.Item>
        <Descriptions.Item label="Bác sĩ">{draft.doctorName}</Descriptions.Item>
        <Descriptions.Item label="Thời gian">
          {shortTime(draft.time)} ngày {dayjs(draft.date).format('DD/MM/YYYY')}
        </Descriptions.Item>
        <Descriptions.Item label="Lý do khám">{draft.reason || '—'}</Descriptions.Item>
        <Descriptions.Item label="Dịch vụ">
          {chosenServices.length ? chosenServices.map((s) => <Tag key={s.maDichVu}>{s.tenDichVu}</Tag>) : '—'}
        </Descriptions.Item>
        <Descriptions.Item label="Tiền sử khai thêm">
          {draft.history.length ? draft.history.map((h, i) => <Tag key={i}>{historyLabel(h.loaiTienSu)}: {h.tenTienSu}</Tag>) : '—'}
        </Descriptions.Item>
      </Descriptions>
      <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Button onClick={onBack} disabled={submitting}>Quay lại</Button>
        <Button type="primary" loading={submitting} onClick={onSubmit}>Xác nhận đặt lịch</Button>
      </div>
    </>
  )
}
