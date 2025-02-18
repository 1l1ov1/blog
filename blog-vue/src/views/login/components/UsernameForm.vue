<script setup>
import { ref, onMounted } from 'vue'
import { getCaptcha } from '@/api/auth'
import { throttle } from '@/utils/debounce-throttle-util'
const prop = defineProps({
    InputType: {
        type: String,
        required: true
    }
})
const form = ref({
    username: '',
    password: '',
    captcha: '',
    captchaKey: ''
})
const formRef = ref(null)
const formRules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 6, max: 15, message: '长度在 6 到 15 个字符', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        {
            pattern: /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/,
            message: '密码需8~15字符，包含大小写字母、数字和特殊符号',
            trigger: 'blur'
        }
    ],
    captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})
const captchaUrl = ref('')
// 获取验证码
const fetchCaptcha = throttle(async () => {
    const response = await getCaptcha();
    // 设置令牌，登录的时候要用这个令牌去redis中查询验证码
    form.value.captchaKey = response.headers['x-captcha-key']
    captchaUrl.value = URL.createObjectURL(response.data)
}, 500)

// 暴露接口方法
const getFormDate = () => {
    return new Promise((resolve) => {
        formRef.value.validate((valid) => {
            if (valid) {
                resolve(form.value);
            } else {
                resolve(null);
            }
        });
    });
};


defineExpose({
    getFormDate
})

onMounted(() => {
    fetchCaptcha();
})
</script>

<template>
    <el-form :rules="formRules" v-show="prop.InputType === 'username'" :model="form" ref="formRef">
        <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" clearable />
        </el-form-item>
        <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" prefix-icon="i-ep-lock" size="large" show-password
                clearable />
        </el-form-item>
        <el-form-item prop="captcha">
            <el-input v-model="form.captcha" placeholder="请输入验证码" prefix-icon="Key" size="large" clearable
                style="width: 60%; margin-right: 2%;" />
            <el-image :src="captchaUrl" style="height: 100%; width: 38%;" @click="fetchCaptcha()" />
        </el-form-item>
    </el-form>
</template>

<style lang='less' scoped></style>
