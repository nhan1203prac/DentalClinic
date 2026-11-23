import { useState } from 'react'
import { Button, Card, DatePicker, Form, Input, Radio, Select, Space, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { historyLabel } from './bookingConstants'

const NEW = 'new'

// Bước 1: chọn người khám có sẵn hoặc nhập người khám mới (BR-03)
export function PatientStep({ patients, draft, onNext }) {
  const [form] = Form.useForm()
  const initial = draft.patientId ?? (draft.newPatient || patients.length === 0 ? NEW : patients[0]?.maBenhNhan)
  const [selected, setSelected] = useState(initial)

  async function next() {
    if (selected !== NEW) {
      onNext({ patientId: selected, newPatient: null })
      return
    }
    const values = await form.validateFields()
    onNext({
      patientId: null,
      newPatient: {
        hoTen: values.hoTen.trim(),
        ngaySinh: values.ngaySinh.format('YYYY-MM-DD'),
        gioiTinh: values.gioiTinh,
        diaChi: values.diaChi,
      },
    })
  }

  const saved = draft.newPatient
  return (
    <>
      <Radio.Group value={selected} onChange={(e) => setSelected(e.target.value)} style={{ width: '100%' }}>
        <Space direction="vertical" style={{ width: '100%' }}>
          {patients.map((p) => (
            <Card key={p.maBenhNhan} size="small">
              <Radio value={p.maBenhNhan}>
                <Typography.Text strong>{p.hoTen}</Typography.Text>{' '}
                <Typography.Text type="secondary">
                  {dayjs(p.ngaySinh).format('DD/MM/YYYY')}
                  {p.gioiTinh ? ` · ${p.gioiTinh}` : ''}
                </Typography.Text>
              </Radio>
              {p.tienSu.length > 0 && (
                <div style={{ marginTop: 8, marginInlineStart: 24 }}>
                  {p.tienSu.map((h, i) => (
                    <Tag key={i}>{historyLabel(h.loaiTienSu)}: {h.tenTienSu}</Tag>
                  ))}
                </div>
              )}
            </Card>
          ))}
          <Card size="small">
            <Radio value={NEW}>Thêm người khám mới</Radio>
          </Card>
        </Space>
      </Radio.Group>

      {selected === NEW && (
        <Form
          form={form}
          layout="vertical"
          requiredMark={false}
          style={{ marginTop: 16 }}
          initialValues={saved ? { ...saved, ngaySinh: dayjs(saved.ngaySinh) } : undefined}
        >
          <Form.Item name="hoTen" label="Họ tên" rules={[{ required: true, whitespace: true, message: 'Nhập họ tên' }, { max: 100, message: 'Tối đa 100 ký tự' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="ngaySinh" label="Ngày sinh" rules={[{ required: true, message: 'Chọn ngày sinh' }]}>
            <DatePicker format="DD/MM/YYYY" disabledDate={(d) => d.isAfter(dayjs())} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="gioiTinh" label="Giới tính">
            <Select allowClear options={['Nam', 'Nữ', 'Khác'].map((v) => ({ value: v, label: v }))} />
          </Form.Item>
          <Form.Item name="diaChi" label="Địa chỉ" rules={[{ max: 255, message: 'Tối đa 255 ký tự' }]}>
            <Input />
          </Form.Item>
        </Form>
      )}

      <div style={{ marginTop: 16, textAlign: 'right' }}>
        <Button type="primary" onClick={next}>Tiếp tục</Button>
      </div>
    </>
  )
}
