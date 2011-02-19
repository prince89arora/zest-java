<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title>Cargo Administration</title>
</head>
<body>
<div id="container">
    <table>
        <caption>Details for cargo ${cargo.trackingId}</caption>
        <tbody>
        <tr>
            <td>Origin</td>
            <td>${cargo.origin}</td>
        </tr>
        <tr>
            <td>Destination</td>
            <td>${cargo.finalDestination}</td>
        </tr>
        </tbody>
    </table>
    <c:choose>
        <c:when test="${cargo.routed}">
            <table>
                <caption>Itinerary</caption>
                <thead>
                <tr>
                    <td>Carrier</td>
                    <td>From</td>
                    <td>To</td>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${cargo.legs}" var="leg">
                    <tr>
                        <td>${leg.carrierMovementId}</td>
                        <td>${leg.from}</td>
                        <td>${leg.to}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>
                <c:url value="/admin/selectItinerary.html" var="selectUrl">
                    <c:param name="trackingId" value="${cargo.trackingId}"/>
                </c:url>
                Not routed - <a href="${selectUrl}">Route this cargo</a>
            </p>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>