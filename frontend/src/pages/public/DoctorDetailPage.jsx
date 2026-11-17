import { Alert, Avatar, Button, Card, Descriptions, Skeleton, Space, Typography } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { ApiError } from '../../api/client'
import { publicApi } from '../../api/publicApi'
import { errorMessage } from '../../utils/formErrors'

export function DoctorDetailPage() {
  const id = Number(useParams().id)
  const { data: doctor, isLoading, error } = useQuery({
    queryKey: ['public', 'doctors', id],
    queryFn: () => publicApi.doctor(id),
    enabled: Number.isInteger(id),
    retry: false,
  })

  if (isLoading) {
    return <Skeleton active avatar paragraph={{ rows: 4 }} />
  }
  if (error || !doctor) {
    const notFound = error instanceof ApiError && error.code === 'DOCTOR_NOT_FOUND'
    return <Alert type="error" showIcon message={notFound ? 'Không tìm thấy bác sĩ' : errorMessage(error)} />
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
