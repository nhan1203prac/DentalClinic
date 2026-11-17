import { useEffect } from 'react'
import { Alert, Avatar, Button, Card, Descriptions, Skeleton, Space, Typography } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useParams } from 'react-router-dom'
import { fetchDoctor } from '../../store/doctorsSlice'

export function DoctorDetailPage() {
  const id = Number(useParams().id)
  const dispatch = useDispatch()
  const { item: doctor, status, error, errorCode } = useSelector((state) => state.doctors.detail)

  useEffect(() => {
    if (Number.isInteger(id)) {
      dispatch(fetchDoctor(id))
    }
  }, [dispatch, id])

  if (!Number.isInteger(id)) {
    return <Alert type="error" showIcon message="Không tìm thấy bác sĩ" />
  }
  if (status === 'idle' || status === 'loading') {
    return <Skeleton active avatar paragraph={{ rows: 4 }} />
  }
  if (status === 'failed' || !doctor) {
    return <Alert type="error" showIcon message={errorCode === 'DOCTOR_NOT_FOUND' ? 'Không tìm thấy bác sĩ' : error} />
  }

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Link to="/bac-si"><Button type="link" style={{ padding: 0 }}>← Danh sách bác sĩ</Button></Link>
      <Card>
        <Space size={24} align="start" wrap>
          <Avatar size={120} src={doctor.anhDaiDien} icon={<UserOutlined />} />
          <div>
            <Typography.Title level={2} style={{ marginTop: 0 }}>{doctor.hoTen}</Typography.Title>
            <Descriptions column={1} size="small">
              <Descriptions.Item label="Chuyên môn">{doctor.chuyenMon ?? '—'}</Descriptions.Item>
              <Descriptions.Item label="Bằng cấp">{doctor.bangCap ?? '—'}</Descriptions.Item>
            </Descriptions>
          </div>
        </Space>
        {doctor.gioiThieu && (
          <>
            <Typography.Title level={4}>Giới thiệu</Typography.Title>
            <Typography.Paragraph style={{ whiteSpace: 'pre-line' }}>{doctor.gioiThieu}</Typography.Paragraph>
          </>
        )}
      </Card>
    </Space>
  )
}
