<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- /WEB-INF/view/ckedit.jsp --%>    
<script>
  window.parent.CKEDITOR.tools.callFunction
    (${CKEditorFuncNum},'${fileName}','이미지 업로드 완료');
</script>