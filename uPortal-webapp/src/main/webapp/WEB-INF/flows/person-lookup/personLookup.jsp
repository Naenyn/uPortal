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
<%@ include file="/WEB-INF/jsp/include.jsp"%>

<portlet:renderURL var="selectPersonUrl" escapeXml="false">
    <portlet:param name="execution" value="${flowExecutionKey}" />
    <portlet:param name="_eventId" value="select"/>
    <portlet:param name="username" value="USERNAME"/>
</portlet:renderURL>

<portlet:renderURL var="cancelUrl">
    <portlet:param name="execution" value="${flowExecutionKey}"/>
    <portlet:param name="_eventId" value="cancel"/>
</portlet:renderURL>

<c:set var="n"><portlet:namespace/></c:set>

<style>
#${n}personBrowser .dataTables_filter, #${n}personBrowser .first.paginate_button, #${n}personBrowser .last.paginate_button{
    display: none;
}
#${n}personBrowser .dataTables-inline, #${n}personBrowser .column-filter-widgets {
    display: inline-block;
}
#${n}personBrowser .view-filter {
    padding-bottom: 15px;
    overflow: hidden;
}
#${n}personBrowser .dataTables_wrapper {
    width: 100%;
}
#${n}personBrowser .dataTables_paginate .paginate_button {
    margin: 2px;
    color: #428BCA;
    cursor: pointer;
    *cursor: hand;
}
#${n}personBrowser .dataTables_paginate .paginate_active {
    margin: 2px;
    color:#000;
}

#${n}personBrowser .dataTables_paginate .paginate_active:hover {
    text-decoration: line-through;
}

#${n}personBrowser table tr td a {
    color: #428BCA;
}

#${n}personBrowser .dataTables-left {
    float:left;
}

#${n}personBrowser .column-filter-widget {
    vertical-align: top;
    display: inline-block;
    overflow: hidden;
    margin-right: 5px;
}

#${n}personBrowser .filter-term {
    display: block;
    text-align:bottom;
}

#${n}personBrowser .dataTables_length label {
    font-weight: normal;
}
#${n}personBrowser .dataTables_length select {
    display: inline-block !important;
    width: auto;
    margin: 0 5px;
}
#${n}personBrowser .datatable-search-view {
    text-align:right;
}

/* Fix form layout to match release version */
#${n}searchForm {
    margin-bottom: 0;
}
#${n}searchForm .row {
    margin: 0;
    align-items: end;
}
#${n}searchForm .col-md-6 {
    padding-left: 0;
    padding-right: 15px;
}
#${n}searchForm .buttons {
    margin-top: 0;
}
/* Fix form control heights to match */
#${n}searchForm .form-select,
#${n}searchForm .form-control {
    height: 34px !important;
    padding: 6px 12px !important;
    font-size: 14px !important;
    line-height: 20px !important;
    box-sizing: border-box !important;
}
#${n}searchForm .form-select {
    padding-right: 30px !important; /* Space for caret */
}
</style>

<!-- Portlet -->
<div id="${n}personBrowser" class="card portlet prs-lkp view-lookup" role="section">

    <!-- Portlet Titlebar -->
    <div class="card-header titlebar portlet-titlebar" role="sectionhead">
        <h2 class="title" role="heading">
            <spring:message code="search.for.users" />
        </h2>
    </div>

    <!-- Portlet Content -->
    <div class="card-body content portlet-content">

        <div class="portlet-content">
            <form id="${n}searchForm" action="javascript:;" class="clearfix">
                <div class="row g-2">
                    <div class="col-md-6">
                        <select id="${n}queryAttribute" class="form-select" name="queryAttribute" aria-label="<spring:message code="type"/>">
                            <option value="">
                                <spring:message code="default.directory.attributes"/>
                            </option>
                            <c:forEach var="queryAttribute" items="${queryAttributes}">
                                <option value="${ queryAttribute }">
                                    <spring:message code="attribute.displayName.${queryAttribute}" text="${queryAttribute}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <input id="${n}queryValue" aria-label="<spring:message code="search.terms"/>" class="form-control" type="search" name="queryValue"/>
                    </div>
                    <div class="col-md-6">
                        <!-- Buttons -->
                        <div class="buttons">
                            <spring:message var="searchButtonText" code="search" />
                            <input class="button btn btn-primary" type="submit" value="${searchButtonText}" />
                            <a class="button btn btn-secondary" href="${ cancelUrl }">
                                <spring:message code="cancel" />
                            </a>
                        </div>
                    </div>
                </div>
            </form>

            <div id="${n}searchResults" class="portlet-section" style="display:none" role="region">
                <div class="titlebar">
                    <h3 role="heading" class="title">Search Results</h3>
                </div>
                <table id="${n}resultsTable" class="portlet-table table table-bordered table-hover" style="width:100%;">
                    <thead>
                        <tr>
                            <th><spring:message code="name"/></th>
                            <th><spring:message code="username"/></th>
                        </tr>
                    </thead>
                </table>
            </div>

        </div>
    </div>

    <script type="text/javascript">
    // Move variables to global scope for debugging
    var attrs = ${up:json(queryAttributes)};
    var personList_configuration = {
        column: {
            name: 0,
            username: 1
        },
        main: {
            table : null,
            pageSize: 10
        }
    };
    
    up.jQuery(function() {
        var $ = up.jQuery;
        
        // Debug namespace
        console.log('Namespace: "${n}"');
        console.log('Form selector: "#${n}searchForm"');
        console.log('Form exists:', $("#${n}searchForm").length > 0);

        // Url generating helper functions
        var getSelectPersonAnchorTag = function(displayName, userName) {
            var url = '${selectPersonUrl}'.replace("USERNAME", userName);
            return '<a href="' + url + '">' + displayName + '</a>';
        };
        var showSearchResults = function (queryData) {
            personList_configuration.main.table = $("#${n}resultsTable").DataTable({
                pageLength: personList_configuration.main.pageSize,
                lengthMenu: [5, 10, 20, 50],
                serverSide: false,
                ajax: {
                    url: '<c:url value="/api/people.json"/>',
                    data: queryData,
                    dataSrc: "people",
                    error: function (xhr, error, thrown) {
                        console.error('AJAX Error:', xhr.status, xhr.responseText, error, thrown);
                    }
                },
                deferRender: false,
                processing: true,
                autoWidth: false,
                pagingType: 'full_numbers',
                language: {
                    lengthMenu: '_MENU_ per page',
                    paginate: {
                        previous: '<spring:message code="datatables.paginate.previous" htmlEscape="false" javaScriptEscape="true"/>',
                        next: '<spring:message code="datatables.paginate.next" htmlEscape="false" javaScriptEscape="true"/>'
                    }
                },
                columns: [
                    { data: 'attributes.displayName', type: 'string', width: '50%' },
                    { data: 'attributes.username', type: 'string', width: '50%' }
                ],
                initComplete: function (settings) {
                    this.api().draw();
                },
                rowCallback: function (row, data, displayNum, displayIndex, dataIndex) {
                    $('td:eq(0)', row).html( getSelectPersonAnchorTag(data.attributes.displayName, data.attributes.username) );
                    $('td:eq(1)', row).html( getSelectPersonAnchorTag(data.attributes.username, data.attributes.username) );
                },
                infoCallback: function( settings, start, end, max, total, pre ) {
                    var infoMessage = '<spring:message code="datatables.info.message" htmlEscape="false" javaScriptEscape="true"/>';
                    var currentPage = Math.ceil(settings._iDisplayStart / settings._iDisplayLength) + 1;
                    infoMessage = infoMessage.replace(/_START_/g, start)
                                        .replace(/_END_/g, end)
                                        .replace(/_TOTAL_/g, total)
                                        .replace(/_CURRENT_PAGE_/g, currentPage);
                    return infoMessage;
                },
                drawCallback: function() {
                    // Convert pagination to match release version structure
                    var $paginate = $('.dataTables_paginate');
                    var $ul = $paginate.find('ul');
                    if ($ul.length) {
                        $ul.find('.paginate_button').each(function() {
                            var $li = $(this);
                            var $link = $li.find('a');
                            if ($link.length) {
                                var newClass = $li.attr('class').replace('page-item', 'paginate_button').replace(/\bpage-link\b/g, '');
                                $link.attr('class', newClass);
                                $paginate.append($link);
                            }
                        });
                        $ul.remove();
                    }
                    
                    // Populate column filters
                    var table = this.api();
                    $('.column-filter-widgets').empty();
                    
                    var nameSelect = $('<select><option value="">Name</option></select>');
                    table.column(0).data().unique().sort().each(function(d) {
                        var displayName = Array.isArray(d) ? d[0] : d;
                        nameSelect.append('<option value="' + displayName + '">' + displayName + '</option>');
                    });
                    nameSelect.on('change', function() {
                        table.column(0).search(this.value).draw();
                    });
                    
                    var usernameSelect = $('<select><option value="">Username</option></select>');
                    table.column(1).data().unique().sort().each(function(d) {
                        var username = Array.isArray(d) ? d[0] : d;
                        usernameSelect.append('<option value="' + username + '">' + username + '</option>');
                    });
                    usernameSelect.on('change', function() {
                        table.column(1).search(this.value).draw();
                    });
                    
                    $('.column-filter-widgets').append(
                        $('<div class="column-filter-widget">').append(nameSelect)
                    ).append(
                        $('<div class="column-filter-widget">').append(usernameSelect)
                    );
                },
                dom: 'r<"row alert alert-info view-filter"<"toolbar-filter"><"column-filter-widgets"><"toolbar-br"><"dataTables-inline dataTables-left"p><"dataTables-inline dataTables-left"i><"dataTables-inline dataTables-left"l>><t>',
            });
            
            // Remove Bootstrap classes from length select to fix display issues
            $('.dataTables_length select').removeClass('form-select form-select-sm');
            
            $("#${n}searchResults").show();
            
            // Adding formatting to sDom to match release version
            $("div.toolbar-br").html('<br>');
            $("div.toolbar-filter").html('<b>Filters</b>:');
        };

        $(function(){
            // Find form dynamically if namespace fails
            var $form = $("#${n}searchForm");
            if ($form.length === 0) {
                $form = $("form[id*='searchForm']");
                console.log('Using fallback form selector, found:', $form.length, 'forms');
            }
            
            // Also find table with fallback
            var $table = $("#${n}resultsTable");
            if ($table.length === 0) {
                $table = $("table[id*='resultsTable']");
                console.log('Using fallback table selector, found:', $table.length, 'tables');
            }
            
            $form.on('submit', function(e) {
                e.preventDefault();
                var queryData = { searchTerms: [] };
                var searchTerm = $("#${n}queryValue").val();
                var queryTerm = $("#${n}queryAttribute").val();

                // if no search term present do not submit form
                if (searchTerm.length == 0) {
                    $("#${n}searchResults").hide();
                    return false;
                }

                if (!queryTerm) {
                    $(attrs).each(function (idx, attr) {
                        queryData.searchTerms.push(attr);
                        queryData[attr] = searchTerm;
                    });
                } else {
                    queryData.searchTerms.push(queryTerm);
                    queryData[queryTerm] = searchTerm;
                }
                
                // To allow the datatable to be repopulated to search multiple times
                // clear and destroy the original
                if (personList_configuration.main.table != undefined && $.fn.DataTable.isDataTable('#${n}resultsTable')) {
                    $("#${n}searchResults").hide();
                    // Properly cleanup DataTables to prevent memory leaks
                    personList_configuration.main.table.clear().draw();
                    personList_configuration.main.table.destroy(); // Don't remove from DOM
                    personList_configuration.main.table = null;
                }
                showSearchResults(queryData);
                return false;
            });
            
            // Cleanup on page unload to prevent memory leaks
            $(window).on('beforeunload', function() {
                if (personList_configuration.main.table != undefined && $.fn.DataTable.isDataTable('#${n}resultsTable')) {
                    personList_configuration.main.table.clear();
                    personList_configuration.main.table.destroy();
                    personList_configuration.main.table = null;
                }
            });
        });
    });
    </script>
</div>
