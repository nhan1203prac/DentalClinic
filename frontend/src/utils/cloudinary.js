const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME
const UPLOAD_PRESET = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET

export const cloudinaryConfigured = Boolean(CLOUD_NAME && UPLOAD_PRESET)

// Tải ảnh thẳng lên Cloudinary (unsigned preset), trả về link công khai để gửi cho backend.
// Dùng fetch thay vì axios của dự án để không gửi kèm cookie/token sang domain khác.
export async function uploadImage(file) {
  const body = new FormData()
  body.append('file', file)
  body.append('upload_preset', UPLOAD_PRESET)
  const res = await fetch(`https://api.cloudinary.com/v1_1/dqmlemao7/image/upload`, { method: 'POST', body })
  const data = await res.json()
  if (!res.ok) {
    throw new Error(data.error?.message ?? 'Tải ảnh lên thất bại')
  }
  return data.secure_url
}
