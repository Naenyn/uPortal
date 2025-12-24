<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<!-- START: VALUES BEING PASSED FROM BACKEND -->
<portlet:actionURL var="queryUrl">
  <portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:actionURL>

<c:set var="n"><portlet:namespace/></c:set>

<portlet:renderURL var="newPortletUrl" >
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="createPortlet"/>
</portlet:renderURL>
<portlet:renderURL var="editPortletUrl" escapeXml="false">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="editPortlet"/>
  <portlet:param name="portletId" value="PORTLETID"/>
</portlet:renderURL>
<portlet:renderURL var="removePortletUrl" escapeXml="false">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="removePortlet"/>
  <portlet:param name="portletId" value="PORTLETID"/>
</portlet:renderURL>
<!-- END: VALUES BEING PASSED FROM BACKEND -->

<!--
PORTLET DEVELOPMENT STANDARDS AND GUIDELINES
| For the standards and guidelines that govern
| the user interface of this portlet
| including HTML, CSS, JavaScript, accessibilty,
| naming conventions, 3rd Party libraries
| (like jQuery and Bootstrap)
| and more, refer to:
| docs/SKINNING_UPORTAL.md
-->

<style>
#${n}portletBrowser .dataTables_filter, #${n}portletBrowser .first.paginate_button, #${n}portletBrowser .last.paginate_button{
    display: none;
}
#${n}portletBrowser .dataTables-inline, #${n}portletBrowser .column-filter-widgets {
    display: inline-block;
}
#${n}portletBrowser .dataTables_wrapper {
    width: 100%;
}
#${n}portletBrowser .dataTables_paginate .paginate_button {
    margin: 2px;
    color: #428BCA;
    cursor: pointer;
    *cursor: hand;
}
#${n}portletBrowser .dataTables_paginate .paginate_active {
    margin: 2px;
    color:#000;
}

#${n}portletBrowser .dataTables_paginate .paginate_active:hover {
    text-decoration: line-through;
}

#${n}portletBrowser table tr td a {
    color: #428BCA;
}

#${n}portletBrowser .dataTables-left {
    float:left;
}

#${n}portletBrowser .column-filter-widget {
    vertical-align: top;
    display: inline-block;
    overflow: hidden;
    margin-right: 5px;
}

#${n}portletBrowser .filter-term {
    display: block;
    text-align:bottom;
}

#${n}portletBrowser .dataTables_length label {
    font-weight: normal;
}
#${n}portletBrowser .datatable-search-view {
    text-align:right;
}
</style>

<!-- Portlet -->
<div id="${n}portletBrowser" class="card portlet ptl-mgr view-home" role="section">
  <c:if test="${not empty statusMsgCode}">
    <div class="alert alert-success alert-dismissable">
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-hidden="true"></button>
      <spring:message code="${statusMsgCode}" arguments="${portlet.name}" htmlEscape="true"/>
      <c:if test="${not empty layoutURL}">
        <spring:message code="add.portlet.to.layout" arguments="${layoutURL}" htmlEscape="false"/>
      </c:if>
    </div>
  </c:if>
  <!-- Portlet Titlebar -->
  <div class="card-header titlebar portlet-titlebar" role="sectionhead">
    <h2 class="title" role="heading"><spring:message code="portlet.registry"/></h2>
    <div class="fl-col-flex2 toolbar" role="toolbar">
      <div class="fl-col">
        <div class="btn-group" role="group">
          <a class="btn btn-primary" href="${ newPortletUrl }" title="<spring:message code="register.new.portlet"/>"><span><spring:message code="register.new.portlet"/></span>&nbsp;&nbsp;<i class="fa fa-plus-circle"></i></a>
        </div>
      </div>
      <div class="fl-col text-end datatable-search-view">
        <form class="portlet-search-form d-flex align-items-center gap-2" style="display:inline">
          <label for="${n}search" class="form-label">
            <spring:message code="search"/>
          </label>
          <input id="${n}search" type="search" class="portlet-search-input form-control"/>
        </form>
      </div>
    </div>
    <div style="clear:both"></div>
  </div>

  <!-- Portlet Content -->
  <div class="card-body content portlet-content">
      <div>
        <table id="${n}portletsList" class="portlet-table table table-bordered table-striped table-hover" style="width:100%;">
          <thead>
            <tr>
              <th><spring:message code="name"/></th>
              <th><spring:message code="type"/></th>
              <th><spring:message code="state"/></th>
              <th><spring:message code="edit"/></th>
              <th><spring:message code="delete"/></th>
              <th><spring:message code="category"/></th>
            </tr>
          </thead>
        </table>
      </div>
  </div> <!-- end: portlet-body -->

</div> <!-- end: portlet -->

<script type="text/javascript">
up.jQuery(function() {

    var $ = up.jQuery;  // de-alias jQuuery to the customary name

    var portletList_configuration = {
        column: {
            name: 0,
            type: 1,
            Lifecycle: 2,
            placeHolderForEditLink  : 3,
            placeHolderForDeleteLink: 4,
            categories: 5
        },
        main: {
            table : null,
            pageSize: 10
        }
    };

    // Url generating helper functions
    var getEditURL = function(portletId) {
        var url = '${editPortletUrl}'.replace("PORTLETID", portletId);
        return '<a href="' + url + '"><spring:message code="edit" htmlEscape="false" javaScriptEscape="true"/> <span class="float-end"><i class="fa fa-edit"></i></span></a>';
    };
    var getDeleteURL = function(portletId) {
        var url = '${removePortletUrl}'.replace("PORTLETID", portletId);
        return '<a href="' + url + '"><spring:message code="delete" htmlEscape="false" javaScriptEscape="true"/> <span class="float-end"><i class="fa fa-trash-o"></i></span></a>';
    };

    // Created as its own
    var initializeTable = function() {
        portletList_configuration.main.table = $("#${n}portletsList").dataTable({
            iDisplayLength: portletList_configuration.main.pageSize,
            aLengthMenu: [5, 10, 20, 50],
            bServerSide: false,
            sAjaxSource: '<c:url value="/api/portlets.json"/>',
            sAjaxDataProp: "portlets",
            bDeferRender: false,
            bProcessing: true,
            bAutoWidth: false,
            sPaginationType: 'full_numbers',
            oLanguage: {
                sLengthMenu: '<spring:message code="datatables.length-menu.message" htmlEscape="false" javaScriptEscape="true"/>',
                oPaginate: {
                    sPrevious: '<spring:message code="datatables.paginate.previous" htmlEscape="false" javaScriptEscape="true"/>',
                    sNext: '<spring:message code="datatables.paginate.next" htmlEscape="false" javaScriptEscape="true"/>'
                }
            },
            aoColumns: [
                { mData: 'name', sType: 'html', sWidth: '30%' },  // Name
                { mData: 'type', sType: 'html', sWidth: '30%' },  // Type
                { mData: 'lifecycleState', sType: 'html', sWidth: '20%' },  // Lifecycle State
                { mData: 'id', sType: 'html', bSearchable: false, sWidth: '10%' },  // Edit Link
                { mData: 'id', sType: 'html', bSearchable: false, sWidth: '10%' },  // Delete Link
                {
                    mData: function(source, type) {
                        // this function sets the value (set), returns original source of value (undefined), and then returns the value
                        if (type === undefined) {
                            return source.categories;
                        } else if (type === 'set') {
                            source.display = source.categories.join();
                            return;
                        }
                        // 'display', 'filter', 'sort', and 'type' all just use the formatted string
                        return source.display;
                    },
                    bSearchable: true,
                    bVisible: false,
                    asSorting: [ "desc", "asc" ]
                }  // Categories - hidden
            ],
            fnInitComplete: function (oSettings) {
                //portletList_configuration.main.table.fnDraw();
                // Adding formatting to sDom
                $("div.toolbar-br").html('<BR>');
                $("div.toolbar-filter").html('<h4><spring:message code="filters" htmlEscape="false" javaScriptEscape="true"/></h4>');
                $(".column-filter-widget select").addClass("form-control");
                
                // Debug SearchPanes
                console.log('DataTable initialized, checking SearchPanes...');
                console.log('Table data:', portletList_configuration.main.table.fnGetData());
                
                // Force SearchPanes rebuild after data loads
                setTimeout(function() {
                    var api = portletList_configuration.main.table.api();
                    if (api.searchPanes) {
                        console.log('Rebuilding SearchPanes...');
                        api.searchPanes.rebuildPane();
                    }
                }, 500);
            },
            fnServerData: function (sUrl, aoData, fnCallback, oSettings) {
                oSettings.jqXHR = $.ajax({
                    url: sUrl,
                    data: aoData,
                    dataType: "json",
                    cache: false,
                    type: oSettings.sServerMethod,
                    success: function (json) {
                        if (json.sError) {
                            oSettings.oApi._fnLog(oSettings, 0, json.sError);
                        }

                        $(oSettings.oInstance).trigger('xhr', [oSettings, json]);
                        fnCallback(json);
                    },
                    error: function (xhr, error, thrown) {
                        lib.handleError(xhr, error, thrown);
                    }
                });
            },
            fnInfoCallback: function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
                var infoMessage = '<spring:message code="datatables.info.message" htmlEscape="false" javaScriptEscape="true"/>';
                var iCurrentPage = Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength) + 1;
                infoMessage = infoMessage.replace(/_START_/g, iStart).
                                      replace(/_END_/g, iEnd).
                                      replace(/_TOTAL_/g, iTotal).
                                      replace(/_CURRENT_PAGE_/g, iCurrentPage);
                return infoMessage;
            },
            // Add links to the proper columns after we get the data
            fnRowCallback: function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                // Create edit and delete links
                $('td:eq(3)', nRow).html( getEditURL(aData.id) );
                $('td:eq(4)', nRow).html( getDeleteURL(aData.id) );
            },
            // Setting the top and bottom controls
            sDom: 'r<"row alert alert-info view-filter"<"toolbar-filter"><"toolbar-filter-options"P><"toolbar-br"><"dataTables-inline dataTables-right"p><"dataTables-inline dataTables-left"i><"dataTables-inline dataTables-left"l>><"row"<"span12"t>>',
            // SearchPanes configuration (modern replacement for ColumnFilterWidgets)
            searchPanes: {
                columns: [
                    {
                        header: 'State',
                        targets: [2]
                    },
                    {
                        header: 'Category', 
                        targets: [5]
                    }
                ],
                cascadePanes: true,
                viewTotal: true,
                threshold: 0.1,
                emptyMessage: 'No filters available',
                initCollapsed: false
            }
        });
    };

    initializeTable();
    // Hide the out of the box search and populate it with our text box
    $('#${n}portletBrowser .portlet-search-input').keyup(function(){
        portletList_configuration.main.table.fnFilter( $(this).val() );
    });
});
</script>
