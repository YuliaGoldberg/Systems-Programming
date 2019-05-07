import sqlite3
import os

databaseexisted = os.path.isfile('schedule.db')
database = sqlite3.connect('schedule.db')
with database:
    cursor = database.cursor()
    cursor.execute("SELECT * FROM courses")
    coursesList = cursor.fetchall()
    isCoursesEmpty = len(coursesList) == 0
    iterationNumber = -1
    while os.path.isfile('schedule.db') and not isCoursesEmpty:
        iterationNumber = iterationNumber + 1
        cursor.execute("SELECT * FROM classrooms")
        classroomsList = cursor.fetchall()
        for classroom in classroomsList:
            id = classroom[0]
            classroomLocation = classroom[1]
            currentCourseInClass = classroom[2]
            occupied = classroom[3]  # if the class is already taken
            cursor.execute("""SELECT * FROM courses WHERE class_id =(?) """, [id]) #fetch all the courses that supposed to be in this classroom
            coursesInClassroom = cursor.fetchall()
            if len(coursesInClassroom) != 0:
                course = coursesInClassroom[0]
                courseId = course[0] #curr course id
                courseName = course[1] #curr course name
                courseStudentGrade = course[2]  #student grade
                courseAmountOfStudents = course[3]  #number_of_students
                courseLength = course[5] #course length
                if occupied == 0:
                    print("(" + str(iterationNumber) + ") " + classroomLocation + ": " + courseName + " is schedule to start")
                    cursor.execute("""UPDATE classrooms SET current_course_id =(?) WHERE id =(?)""", [courseId, id])  # update the classroom with the current course
                    cursor.execute("""UPDATE classrooms SET current_course_time_left =(?) WHERE id =(?)""", [courseLength, id])  # update the classroom with the current course length
                    cursor.execute("""UPDATE students SET count = count - (?)  WHERE grade =(?) """, [courseAmountOfStudents, courseStudentGrade])
                else:
                    cursor.execute("""SELECT * FROM courses WHERE id = (?)""", [currentCourseInClass])
                    courseInClass = cursor.fetchone()
                    coursesInClassName = courseInClass[1]
                    if classroom[3] > 1:
                        print("(" + str(iterationNumber) + ") " + classroomLocation + ": occupied by " + str(coursesInClassName))
                    cursor.execute("""UPDATE classrooms SET current_course_time_left = current_course_time_left - 1 WHERE id = (?)""", [id])
                    cursor.execute("""SELECT * FROM classrooms WHERE id = (?)""", [id])
                    tempClassroom = cursor.fetchone()
                    if tempClassroom[3] == 0:
                        print("(" + str(iterationNumber) + ") " + classroomLocation + ": " + courseName + " is done")
                        cursor.execute("""UPDATE classrooms SET current_course_id = 0 WHERE id = (?)""", [tempClassroom[0]])
                        cursor.execute("""DELETE from courses WHERE id = (?)""", [tempClassroom[2]])
                        cursor.execute("""SELECT * FROM courses WHERE class_id = (?)""", [id]) #fetch all the courses that supposed to be in this classroom
                        coursesInClassroom = cursor.fetchall()
                        if len(coursesInClassroom) != 0:
                            course = coursesInClassroom[0]
                            courseId = course[0]
                            courseName = course[1]
                            courseStudentGrade = course[2]
                            courseAmountOfStudents = course[3]
                            courseLength = course[5]
                            print("(" + str(iterationNumber) + ") " + classroomLocation + ": " + courseName + " is schedule to start")
                            cursor.execute("""UPDATE classrooms SET current_course_id = (?) WHERE id = (?)""", [courseId, id])  # update the classroom with the current course

                            cursor.execute("""UPDATE classrooms SET current_course_time_left = (?) WHERE id = (?)""", [courseLength, id])  # update the classroom with the current course length
                            cursor.execute("""UPDATE students SET count = count - (?) WHERE grade =(?) """, [courseAmountOfStudents, courseStudentGrade])
        cursor.execute("SELECT * FROM courses")
        coursesList = cursor.fetchall()
        isCoursesEmpty = len(coursesList) == 0
        if not isCoursesEmpty:
            cursor.execute("SELECT * FROM courses")
            coursesList = cursor.fetchall()
            print("courses")
            for course in coursesList:
                print(course)
            cursor.execute("SELECT * FROM classrooms")
            classroomsList = cursor.fetchall()
            print("classrooms")
            for classroom in classroomsList:
                print(classroom)
            cursor.execute("SELECT * FROM students")
            studentsList = cursor.fetchall()
            print("students")
            for student in studentsList:
                print(student)
            isCoursesEmpty = len(coursesList) == 0
    if isCoursesEmpty:
        cursor.execute("SELECT * FROM courses")
        coursesList = cursor.fetchall()
        print("courses")
        for course in coursesList:
            print(course)
        cursor.execute("SELECT * FROM classrooms")
        classroomsList = cursor.fetchall()
        print("classrooms")
        for classroom in classroomsList:
            print(classroom)
        cursor.execute("SELECT * FROM students")
        studentsList = cursor.fetchall()
        print("students")
        for student in studentsList:
            print(student)
        isCoursesEmpty = len(coursesList) == 0
    database.commit()
