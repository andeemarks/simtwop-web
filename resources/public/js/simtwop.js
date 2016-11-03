$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); 
});

var selectedProjectRole = null;

const STAFF_COUNT_CELL = ".staffing_table > tbody > tr > td.staffing_plans_role_cell_count";
const PROJECT_ROLE_ROW = ".staffing_plans_project > .project_table > tbody > tr";
const PROJECT_ROLE_ROW_UNDO = ".staffing_plans_project > .project_table > tbody > tr > td.staffing_plans_undo";

$(PROJECT_ROLE_ROW).click(function() {
  selectProjectRole(this);
});

function selectProjectRole(selectedRole) {
  $(PROJECT_ROLE_ROW).removeClass("project_role_selected_row");
  $(selectedRole).addClass("project_role_selected_row");
  logAction("Selected '" + gradeRoleTupleToString($(selectedRole).attr('id')) + "' role...");
  selectedProjectRole = $(selectedRole);  
}

$(PROJECT_ROLE_ROW_UNDO).click(function(event) {
  unassignStaffFromSelectedRole($(this));
  event.stopPropagation();
});

function unassignStaffFromSelectedRole(roleToUnassign) {
  roleToUnassign.parent('tr.project_role_assigned_row').each(function() {
    $(this).removeClass("project_role_assigned_row");
    $(this).find('td.open_role').each (function() {
      $(this).removeClassRegex(/staffed_role$/);
    });                    
    var assignedStaffTitle = gradeRoleTupleToString($(this).attr('id'));
    logAction("Unassigned " + assignedStaffTitle);
    //TODO need to put unassigned staff back in pool
  });
}

$(STAFF_COUNT_CELL).click(function() {
  if (selectedProjectRole != null) {
    assignStaffToSelectedRole(this);
  }
});

function assignStaffToSelectedRole(selectedStaff) {
  var currentCount = $(selectedStaff).text();
  if (currentCount > 0) {
    $(selectedStaff).text(currentCount - 1);

    $('<input>').attr({
      type: 'hidden',
      name: "count-" + $(selectedStaff).attr('id'),
      id: "count-" + $(selectedStaff).attr('id'),
      value: (currentCount - 1)
      }).appendTo($(selectedStaff));
    var assignedStaffTitle = gradeRoleTupleToString($(selectedStaff).attr('id'));
    logAction("Assigned " + assignedStaffTitle + " - " + (currentCount - 1) + " remaining");
    $(selectedProjectRole).addClass("project_role_assigned_row");
    assignmentAssessment = assessAssignment(selectedProjectRole[0].id, selectedStaff.id);
    // Update project table
    $(selectedProjectRole).find('td.open_role').each (function() {
      $(this).removeClassRegex(/staffed_role$/);
      $(this).addClass(assignmentAssessment);
    });                    
    // Update scoreboard
    $(".scoreboard").find("tr#" + selectedProjectRole[0].id + ' td.scoreboard_role_assessment').each (function() {
      $(this).removeClassRegex(/staffed_role$/);
      $(this).addClass(assignmentAssessment);
    });                    

    // Add tooltip showing assignment
    $(selectedProjectRole).find('td.staffing_plans_role_title_cell a').each (function() {
      $(this).attr('data-original-title', "Assigned: " + assignedStaffTitle).tooltip('hide');
    });      

    $('<input>').attr({
      type: 'hidden',
      id: "role-" + selectedProjectRole[0].id,
      name: "role-" + selectedProjectRole[0].id,
      value: "assigned-" + $(selectedStaff).attr('id')
      }).appendTo('#game-form');
  }
}

function logAction(actionText) {
  var actionLog = document.getElementById("action_log");
  var action = document.createElement("li");
  action.appendChild(document.createTextNode(actionText));
  actionLog.insertBefore(action, actionLog.childNodes[0]);
}

function gradeRoleTupleToString(gradeRoleTuple) {
  var tupleBits = gradeRoleTuple.split("_");
  return s(tupleBits[0] + " " + tupleBits[1]).titleize().value();
}

function assessAssignment(selectedProjectRole, selectedStaff) {
  var selectedStaffTuple = selectedStaff.split("_");
  var selectedProjectRoleTuple = selectedProjectRole.split("_");

  var grades = ["grad", "con", "senior", "lead", "principal"];
  var staffGrade = selectedStaffTuple[0];
  var staffGradeRank = _.indexOf(grades, staffGrade);
  var staffRole = selectedStaffTuple[1];
  var projectGrade = selectedProjectRoleTuple[0];
  var projectGradeRank = _.indexOf(grades, projectGrade);
  var projectRole = selectedProjectRoleTuple[1];

  var gradeGap = projectGradeRank - staffGradeRank;
  if (staffRole == projectRole) {
    if (staffGrade == projectGrade) {
      return "staffed_role";
    } 

    if (gradeGap > 1) {
      return "understaffed_role";
    }

    if (gradeGap == 1) {
      return "stretchstaffed_role";
    }

    if (gradeGap == -1) {
      return "minoroverstaffed_role";
    }

    if (gradeGap < -1) {
      return "majoroverstaffed_role";
    }
  } else {
    if (gradeGap < 0) {
      return "stretchstaffed_role";      
    }    
  }

  return "misstaffed_role";

}

$.fn.removeClassRegex = function(regex) {
  return $(this).removeClass(function(index, classes) {
    return classes.split(/\s+/).filter(function(c) {
      return regex.test(c);
    }).join(' ');
  });
};

function toggleChevron(e) {
  $(e.target)
    .prev('.staffing_plans_project_header')
    .find('i.indicator')
    .toggleClass('fa-chevron-circle-right fa-chevron-circle-down');
}
$('.old_project').on('hidden.bs.collapse', toggleChevron);
$('.old_project').on('shown.bs.collapse', toggleChevron);
