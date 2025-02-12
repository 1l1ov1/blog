<script setup>
import { ref } from 'vue'

const loginForm = ref({
    loginType: 'phone', // phone/email
    phone: '',
    email: '',
    password: '',
    captcha: ''
})

const loginTypes = [
    { value: 'phone', label: '手机登录', icon: 'icon-mobile' },
    { value: 'email', label: '邮箱登录', icon: 'icon-email' }
]

const handleSwitchLogin = (type) => {
    loginForm.value.loginType = type
    // 清空切换后的输入内容
    loginForm.value.phone = ''
    loginForm.value.email = ''
}
</script>

<template>
    <div class="container">
        <div class="form-box">
            <div class="login-form">
                <!-- 动态背景形状 -->
                <div class="decorate-bg">
                    <div class="shape square"></div>
                    <div class="shape circle"></div>
                    <div class="shape triangle"></div>
                </div>

                <div class="form-content">
                    <div class="form-header">
                        <h1>Welcome Back</h1>
                        <p>选择你的登录方式</p>
                    </div>

                    <!-- 登录方式切换 -->
                    <div class="login-type-switch">
                        <div v-for="item in loginTypes" :key="item.value" class="type-item"
                            :class="{ active: loginForm.loginType === item.value }"
                            @click="handleSwitchLogin(item.value)">
                            <i class="iconfont" :class="item.icon"></i>
                            <span>{{ item.label }}</span>
                        </div>
                    </div>

                    <!-- 登录表单 -->
                    <el-form :model="loginForm" class="form-group">
                        <!-- 手机登录 -->
                        <el-form-item v-if="loginForm.loginType === 'phone'">
                            <el-input v-model="loginForm.phone" placeholder="请输入手机号码" size="large" class="custom-input">
                                <template #prefix>
                                    <i class="iconfont icon-mobile"></i>
                                </template>
                                <template #append>
                                    <el-select v-model="areaCode" placeholder="+86" style="width: 90px"
                                        class="area-select">
                                        <el-option label="+86" value="+86" />
                                        <el-option label="+852" value="+852" />
                                        <el-option label="+853" value="+853" />
                                    </el-select>
                                </template>
                            </el-input>
                        </el-form-item>

                        <!-- 邮箱登录 -->
                        <el-form-item v-else>
                            <el-input v-model="loginForm.email" placeholder="请输入邮箱地址" size="large" class="custom-input">
                                <template #prefix>
                                    <i class="iconfont icon-email"></i>
                                </template>
                            </el-input>
                        </el-form-item>

                        <!-- 公共表单项 -->
                        <el-form-item>
                            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password
                                size="large" class="custom-input">
                                <template #prefix>
                                    <i class="iconfont icon-lock"></i>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item>
                            <div class="captcha-box">
                                <el-input v-model="loginForm.captcha" placeholder="验证码" size="large"
                                    class="custom-input" style="width: 65%" />
                                <div class="captcha-img">
                                    <img src="https://picsum.photos/100/40" alt="captcha">
                                    <i class="iconfont icon-refresh"></i>
                                </div>
                            </div>
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" class="login-btn" @click="handleLogin">
                                <i class="iconfont icon-login"></i>
                                立即登录
                            </el-button>
                        </el-form-item>

                        <div class="form-footer">
                            <span>其他登录方式</span>
                            <div class="social-login">
                                <i class="iconfont icon-wechat"></i>
                                <i class="iconfont icon-qq"></i>
                                <i class="iconfont icon-github"></i>
                            </div>
                        </div>
                    </el-form>
                </div>
            </div>
        </div>
    </div>
</template>


<style lang="less" scoped>
.container {
    width: 100vw;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    overflow: hidden;
    position: relative;

    &::before {
        content: '';
        position: absolute;
        width: 400px;
        height: 400px;
        background: linear-gradient(#ff6b6b, #ff8e53);
        border-radius: 50%;
        transform: translate(-50%, -50%);
        top: 30%;
        left: 20%;
        filter: blur(80px);
        opacity: 0.4;
    }

    &::after {
        content: '';
        position: absolute;
        width: 350px;
        height: 350px;
        background: linear-gradient(#4bc0c8, #c779d0);
        border-radius: 50%;
        transform: translate(50%, 50%);
        bottom: 0;
        right: 20%;
        filter: blur(80px);
        opacity: 0.4;
    }
}

.form-box {
    width: 800px;
    height: 500px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
    position: relative;
    z-index: 1;
    border: 1px solid rgba(255, 255, 255, 0.1);
}

.login-form {
    padding: 40px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .form-header {
        text-align: center;
        margin-bottom: 40px;

        h1 {
            color: #fff;
            font-size: 2.5rem;
            margin-bottom: 10px;
            letter-spacing: 1px;
        }

        p {
            color: rgba(255, 255, 255, 0.8);
            font-size: 1rem;
            margin-top: 8px;
        }
    }

    .login-type-switch {
        display: flex;
        justify-content: center;
        gap: 20px;
        margin-bottom: 20px;

        .el-button {
            padding: 10px 20px;
            font-size: 16px;
            letter-spacing: 1px;
            background: transparent;
            border: none;
            color: #fff;
            transition: all 0.3s ease;

            &:hover {
                color: #667eea;
            }

            &.is-primary {
                color: #667eea;
                font-weight: bold;
            }
        }
    }

    .form-group {
        .custom-input {
            :deep(.el-input__wrapper) {
                background: rgba(255, 255, 255, 0.1);
                border: none;
                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
                border-radius: 8px;
                padding: 12px 20px;
                transition: all 0.3s ease;

                &:hover {
                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
                }

                &.is-focus {
                    background: rgba(255, 255, 255, 0.2);
                    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
                }
            }

            :deep(.el-input__inner) {
                color: #fff;

                &::placeholder {
                    color: rgba(255, 255, 255, 0.6);
                }
            }

            .iconfont {
                color: rgba(255, 255, 255, 0.6);
                font-size: 18px;
                margin-right: 10px;
            }
        }

        .captcha-box {
            display: flex;
            gap: 15px;
            width: 100%;

            .captcha-img {
                position: relative;
                flex: 1;
                height: 40px;
                border-radius: 8px;
                overflow: hidden;
                cursor: pointer;
                transition: transform 0.3s ease;

                &:hover {
                    transform: scale(1.02);

                    .icon-refresh {
                        opacity: 1;
                    }
                }

                img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }

                .icon-refresh {
                    position: absolute;
                    right: 5px;
                    top: 50%;
                    transform: translateY(-50%);
                    color: #fff;
                    font-size: 16px;
                    opacity: 0.6;
                    transition: opacity 0.3s ease;
                }
            }
        }

        .login-btn {
            width: 100%;
            height: 45px;
            font-size: 16px;
            letter-spacing: 1px;
            background: linear-gradient(45deg, #667eea, #764ba2);
            border: none;
            transition: all 0.3s ease;

            &:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }

            .iconfont {
                margin-right: 8px;
                font-size: 16px;
            }
        }

        .form-footer {
            text-align: center;
            margin-top: 20px;
            color: rgba(255, 255, 255, 0.8);

            .register-btn {
                color: #a3bffa;
                padding: 0 5px;

                &:hover {
                    color: #667eea;
                }
            }
        }
    }
}

.login-type-switch {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
    justify-content: center;

    .type-item {
        display: flex;
        align-items: center;
        padding: 12px 25px;
        border-radius: 8px;
        background: rgba(255, 255, 255, 0.08);
        cursor: pointer;
        transition: all 0.3s ease;
        border: 1px solid transparent;

        &:hover {
            background: rgba(255, 255, 255, 0.15);
        }

        &.active {
            background: rgba(255, 255, 255, 0.2);
            border-color: rgba(255, 255, 255, 0.3);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .iconfont {
            font-size: 18px;
            margin-right: 8px;
            color: rgba(255, 255, 255, 0.8);
        }

        span {
            color: rgba(255, 255, 255, 0.9);
            font-weight: 500;
        }
    }
}

.area-select {
    :deep(.el-input__wrapper) {
        background: transparent;
        box-shadow: none !important;

        .el-input__inner {
            color: rgba(255, 255, 255, 0.8);
        }
    }
}

.social-login {
    display: flex;
    gap: 20px;
    margin-top: 15px;

    .iconfont {
        font-size: 24px;
        color: rgba(255, 255, 255, 0.7);
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
            transform: translateY(-3px);
            color: #fff;

            &.icon-wechat {
                color: #2aae67;
            }

            &.icon-qq {
                color: #12b7f5;
            }

            &.icon-github {
                color: #333;
            }
        }
    }
}
</style>