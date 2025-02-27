<script setup>
import { ref } from 'vue'
const prop = defineProps({
    InputType: {
        type: String,
        required: true
    }
})
const form = ref({
    phone: '',
    captcha: ''
})
const formRules = ref({
    phone: [
        {
            required: true,
            message: '请输入手机号',
            trigger: 'blur'
        },
        {
            pattern: /^1[3-9]\d{9}$/,
            message: '请输入有效的11位手机号码',
            trigger: 'blur'
        }
    ],
    captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
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
    <el-form :rules="formRules" v-show="InputType === 'phone'" :model="form" ref="formRef">
        <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" prefix-icon="Iphone" size="large" clearable />
        </el-form-item>
        <el-form-item prop="captcha">
            <el-input v-model="form.captcha" placeholder="请输入验证码" prefix-icon="Key" size="large" clearable
                style="width: 60%; margin-right: 5%;" />
            <el-button type="primary" size="large" style="width: 35%;">
                获取验证码
            </el-button>
        </el-form-item>
    </el-form>
</template>

<style lang='less' scoped></style>
