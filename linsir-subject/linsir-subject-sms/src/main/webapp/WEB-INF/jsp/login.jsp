<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="cn">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>欢迎进入学生管理系统</title>
    <!-- plugins:css -->
    <link  href=" ${pageContext.request.contextPath}/statics/vendors/mdi/css/materialdesignicons.min.css" media="all">
    <link  href=" ${pageContext.request.contextPath}/statics/vendors/base/vendor.bundle.base.css" media="all">
    <!-- endinject -->
    <!-- plugin css for this page -->
    <!-- End plugin css for this page -->
    <!-- inject:css -->
    <link rel="stylesheet"  type="text/css" href="${pageContext.request.contextPath}/statics/css/style.css">
    <!-- endinject -->
    <%--<link rel="shortcut icon"  href="${pageContext.request.contextPath}/static/images/favicon.png" />--%>
</head>
<body>
<div class="container-scroller">
    <div class="container-fluid page-body-wrapper full-page-wrapper">
        <div class="content-wrapper d-flex align-items-stretch auth auth-img-bg">
            <div class="row flex-grow">
                <div class="col-lg-6 d-flex align-items-center justify-content-center">
                    <div class="auth-form-transparent text-left p-3">
                        <div class="brand-logo">
                            <img src="${pageContext.request.contextPath}/statics/images/logo.png" alt="logo">
                        </div>
                        <h4>欢迎进入学生管理系统</h4>
                        <h6 class="font-weight-light">Happy to see you again!</h6>
                        <form class="pt-3">
                            <div class="form-group">
                                <label for="exampleInputEmail">用户名</label>
                                <div class="input-group">
                                    <div class="input-group-prepend bg-transparent">
                      <span class="input-group-text bg-transparent border-right-0">
                        <i class="mdi mdi-account-outline text-primary"></i>
                      </span>
                                    </div>
                                    <input type="text" class="form-control form-control-lg border-left-0" id="exampleInputEmail" placeholder="Username">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="exampleInputPassword">密码</label>
                                <div class="input-group">
                                    <div class="input-group-prepend bg-transparent">
                      <span class="input-group-text bg-transparent border-right-0">
                        <i class="mdi mdi-lock-outline text-primary"></i>
                      </span>
                                    </div>
                                    <input type="password" class="form-control form-control-lg border-left-0" id="exampleInputPassword" placeholder="Password">
                                </div>
                            </div>
                            <div class="my-2 d-flex justify-content-between align-items-center">
                                <div class="form-check">
                                    <label class="form-check-label text-muted">
                                        <input type="checkbox" class="form-check-input">
                                        保持登录
                                    </label>
                                </div>
                                <a href="#" class="auth-link text-black">Forgot password?</a>
                            </div>
                            <div class="my-3">
                                <a class="btn btn-block btn-primary btn-lg font-weight-medium auth-form-btn" href="../../index.html">LOGIN</a>
                            </div>
                            <div class="mb-2 d-flex">
                                <button type="button" class="btn btn-facebook auth-form-btn flex-grow me-1">
                                    <i class="mdi mdi-facebook me-2"></i>Facebook
                                </button>
                                <button type="button" class="btn btn-google auth-form-btn flex-grow ms-1">
                                    <i class="mdi mdi-google me-2"></i>Google
                                </button>
                            </div>
                            <div class="text-center mt-4 font-weight-light">
                                Don't have an account? <a href="register-2.html" class="text-primary">Createxxxx</a>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="col-lg-6 login-half-bg d-flex flex-row">
                    <p class="text-white font-weight-medium text-center flex-grow align-self-end">Copyright &copy; 2024  All rights reserved.</p>
                </div>
            </div>
        </div>
        <!-- content-wrapper ends -->
    </div>
    <!-- page-body-wrapper ends -->
</div>
<!-- container-scroller -->
<!-- plugins:js -->
<script type="javascript"  src="${pageContext.request.contextPath}/statics/vendors/base/vendor.bundle.base.js"></script>
<!-- endinject -->
<!-- inject:js -->
<script type="javascript"  src="${pageContext.request.contextPath}/statics/js/off-canvas.js"></script>
<script type="javascript"  src="${pageContext.request.contextPath}/statics/js/hoverable-collapse.js"></script>
<script type="javascript"  src="${pageContext.request.contextPath}/statics/js/template.js"></script>
<!-- endinject -->
</body>
</html>
