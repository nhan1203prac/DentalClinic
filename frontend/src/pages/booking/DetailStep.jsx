import { useEffect } from 'react'
import { Alert, Button, Form, Input, Select, Space, Tag, Typography } from 'antd'
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons'
import { useDispatch, useSelector } from 'react-redux'
import { fetchServices } from '../../store/servicesSlice'
import { formatVnd } from '../../utils/format'
import { HISTORY_TYPES, historyLabel } from './bookingConstants'

// Bước 2: tiền sử khai thêm, lý do khám, dịch vụ (không bắt buộc, BR-24)
export function DetailStep({ patient, draft, onBack, onNext }) {
  const dispatch = useDispatch()
  const [form] = Form.useForm()
  const { items: services, status } = useSelector((state) => state.services)

  useEffect(() => {
    dispatch(fetchServices())
  }, [dispatch])

  async function next() {
    const values = await form.validateFields()
    onNext({
      history: (values.tienSu ?? []).filter((h) => h?.loaiTienSu && h.tenTienSu?.trim()),
      reason: values.lyDoKham?.trim() ?? '',
      serviceIds: values.dichVu ?? [],
    })
  }

  return (
    <>
      {patient?.tienSu.length > 0 && (
        <div style={{ marginBottom: 16 }}>
          <Typography.Text type="secondary">Tiền sử đã lưu của {patient.hoTen}: </Typography.Text>
          {patient.tienSu.map((h, i) => (
            <Tag key={i}>{historyLabel(h.loaiTienSu)}: {h.tenTienSu}</Tag>
          ))}
          <Typography.Paragraph type="secondary" style={{ marginTop: 4 }}>
            Tiền sử cũ không sửa ở đây; chỉ thêm mục mới bên dưới nếu có thay đổi.
          </Typography.Paragraph>
        </div>
      )}
      <Form
        form={form}
        layout="vertical"
        requiredMark={false}
        initialValues={{ tienSu: draft.history, lyDoKham: draft.reason, dichVu: draft.serviceIds }}
      >
        <Form.Item name="lyDoKham" label="Lý do khám" rules={[{ max: 500, message: 'Tối đa 500 ký tự' }]}>
          <Input.TextArea rows={3} placeholder="Ví dụ: đau răng hàm dưới bên phải" />
        </Form.Item>
        <Form.Item name="dichVu" label="Dịch vụ muốn đăng ký (không bắt buộc)">
          <Select
            mode="multiple"
            allowClear
            loading={status === 'loading'}
            placeholder="Có thể bỏ trống, bác sĩ sẽ tư vấn khi khám"
            optionFilterProp="label"
            options={services.map((s) => ({ value: s.maDichVu, label: `${s.tenDichVu} – ${formatVnd(s.gia)}` }))}
          />
        </Form.Item>
        <Typography.Text strong>Tiền sử cần báo cho bác sĩ</Typography.Text>
        <Form.List name="tienSu">
          {(fields, { add, remove }) => (
            <div style={{ marginTop: 8 }}>
              {fields.map(({ key, name }) => (
                <Space key={key} align="start" wrap style={{ display: 'flex', marginBottom: 8 }}>
                  <Form.Item name={[name, 'loaiTienSu']} noStyle initialValue="di_ung">
                    <Select style={{ width: 190 }} options={HISTORY_TYPES} />
                  </Form.Item>
                  <Form.Item name={[name, 'tenTienSu']} noStyle rules={[{ max: 150, message: 'Tối đa 150 ký tự' }]}>
                    <Input placeholder="Tên (vd: Penicillin)" style={{ width: 220 }} />
                  </Form.Item>
                  <Form.Item name={[name, 'ghiChu']} noStyle>
                    <Input placeholder="Ghi chú" style={{ width: 200 }} />
                  </Form.Item>
                  <Button type="text" danger icon={<MinusCircleOutlined />} onClick={() => remove(name)} aria-label="Xóa mục" />
                </Space>
              ))}
              <Button icon={<PlusOutlined />} onClick={() => add({ loaiTienSu: 'di_ung' })}>Thêm tiền sử</Button>
            </div>
          )}
        </Form.List>
      </Form>
      {status === 'failed' && <Alert type="warning" showIcon message="Không tải được danh sách dịch vụ, bạn vẫn có thể tiếp tục" style={{ marginTop: 16 }} />}
      <div style={{ marginTop: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Button onClick={onBack}>Quay lại</Button>
        <Button type="primary" onClick={next}>Tiếp tục</Button>
      </div>
    </>
  )
}
