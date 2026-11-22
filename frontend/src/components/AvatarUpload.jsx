import { useState } from 'react'
import { Avatar, Button, Space, Typography, Upload, message } from 'antd'
import { UploadOutlined, UserOutlined } from '@ant-design/icons'
import { cloudinaryConfigured, uploadImage } from '../utils/cloudinary'

const MAX_SIZE_MB = 2

// Dùng trong Form.Item: value là link ảnh, onChange nhận link mới (hoặc null khi xóa)
export function AvatarUpload({ value, onChange }) {
  const [uploading, setUploading] = useState(false)

  function beforeUpload(file) {
    if (!file.type.startsWith('image/')) {
      message.error('Chỉ chọn file ảnh')
      return Upload.LIST_IGNORE
    }
    if (file.size > MAX_SIZE_MB * 1024 * 1024) {
      message.error(`Ảnh tối đa ${MAX_SIZE_MB}MB`)
      return Upload.LIST_IGNORE
    }
    return true
  }

  async function handleUpload({ file }) {
    setUploading(true)
    try {
      onChange?.(await uploadImage(file))
    } catch (e) {
      message.error(e.message)
    } finally {
      setUploading(false)
    }
  }

  return (
    <Space align="center" size={16}>
      <Avatar size={72} src={value} icon={<UserOutlined />} />
      <Space direction="vertical" size={4}>
        <Space>
          <Upload accept="image/*" showUploadList={false} beforeUpload={beforeUpload} customRequest={handleUpload} disabled={!cloudinaryConfigured}>
            <Button icon={<UploadOutlined />} loading={uploading} disabled={!cloudinaryConfigured}>Tải ảnh lên</Button>
          </Upload>
          {value && <Button danger onClick={() => onChange?.(null)}>Xóa ảnh</Button>}
        </Space>
        {!cloudinaryConfigured && (
          <Typography.Text type="warning">Chưa cấu hình Cloudinary trong .env.local</Typography.Text>
        )}
      </Space>
    </Space>
  )
}
