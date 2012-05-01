<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://washingmachine/functions" prefix="f"%>

<table id="gaps-table" class="ui-widget-content">
   <thead>
      <th style="width: 2%;">id</th>
      <th style="width: 2%;">version</th>
      <th style="width: 2%;">description</th>

      <c:set var="alt" value="true" />
      <c:forEach items="${it.days}" var="day">
         <c:if test="${day.day == 1}" var="alt" />
         <fmt:formatDate value="${day}" type="date" pattern="yyyy-MM-dd" var="dayf" />
         <fmt:formatDate value="${day}" type="date" pattern="dd-MM-yyyy" var="dayv" />
         <%--dateStyle="medium" --%>
         <c:choose>
            <c:when test="${alt}">
               <th style="width: 1%; font-weight: bold" day="${dayf}">${dayv}</th>
            </c:when>
            <c:otherwise>
               <th style="width: 1%; font-weight: normal" day="${dayf}">${dayv}</th>
            </c:otherwise>
         </c:choose>
      </c:forEach>
   </thead>

   <tbody>
      <c:forEach items="${it.gaps}" var="gap">
         <c:choose>
            <c:when test="${f:isEmpty(gap)}">
               <tr gap="${gap.id}" style="height: 21px;" class="empty">
            </c:when>
            <c:otherwise>
               <tr gap="${gap.id}" style="height: 21px;">
            </c:otherwise>
         </c:choose>

         <td><a class="edit-gap" href="${gap.link}">${gap.id}</a></td>
         <td style="text-align: center">${gap.version}</td>
         <td>${gap.description}</td>

         <c:forEach items="${it.days}" var="day">
            <c:choose>
               <c:when test="${f:isEmpty(gap)}">
                  <td style="text-align: center"><label class="editable_select"></label></td>
               </c:when>
               <c:otherwise>
                  <!--  -->
                  <c:set var="added" value="false" />
                  <td style="text-align: center">
                     <!--  --> <c:forEach items="${gap.activities}" var="activity">
                        <c:if test="${f:equals(activity.day,day)}">
                           <c:set var="added" value="true" />
                           <label class="editable_select" activity="${activity.link}">${activity.time}</label>
                        </c:if>
                     </c:forEach>
                     <!--  -->
                     <c:if test="${not added}">
                        <label class="editable_select"></label>
                     </c:if>
                  </td>
               </c:otherwise>
            </c:choose>
         </c:forEach>

         </tr>
      </c:forEach>

   </tbody>
</table>
