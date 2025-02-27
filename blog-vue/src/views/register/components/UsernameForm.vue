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
    rePassword: ''
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
    rePassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请输入确认密码'));
                } else if (value !== form.value.password) {
                    callback(new Error('两次输入密码不一致'));
                } else {
                    callback();
                }
            },
            trigger: 'blur'
        }
    ],
})

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
        <el-form-item prop="rePassword">
            <el-input v-model="form.rePassword" placeholder="请输入确认密码" prefix-icon="i-ep-lock" size="large" show-password
                clearable />
        </el-form-item>
    </el-form>
</template>

<style lang='less' scoped></style>
