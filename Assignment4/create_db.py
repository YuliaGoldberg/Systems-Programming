import sqlite3
import sys
import os


def main():
    databaseexisted = os.path.isfile('schedule.db')
    database = sqlite3.connect('schedule.db')
    with database:
        cursor = database.cursor()
        if not databaseexisted:
            cursor.execute("CREATE TABLE classrooms(id INTEGER PRIMARY KEY, location TEXT NOT NULL,"
                           " current_course_id INTEGER NOT NULL, current_course_time_left INTEGER NOT NULL)")
            cursor.execute("CREATE TABLE courses(id INTEGER PRIMARY KEY, course_name TEXT NOT NULL,"
                           "student TEXT NOT NULL, number_of_students INTEGER NOT NULL,"
                           "class_id INTEGER REFERENCES classrooms(id), course_length INTEGER NOT NULL)")
            cursor.execute("CREATE TABLE students(grade TEXT PRIMARY KEY, count INTEGER NOT NULL)")
            inputfilename = sys.argv[1]
            with open(inputfilename) as inputfile:
                for line in inputfile:
                    words = line.split(",")
                    if words[0].strip() == "C":
                        cursor.execute("INSERT INTO courses VALUES(?,?,?,?,?,?)",
                               (int(words[1].strip()), words[2].strip(), words[3].strip(), int(words[4].strip()), int(words[5].strip()), int(words[6].strip())))
                    elif words[0].strip() == "S":
                        cursor.execute("INSERT INTO students VALUES(?,?)",
                               (words[1].strip(), int(words[2].strip())))
                    elif words[0].strip() == "R":
                        cursor.execute("INSERT INTO classrooms VALUES(?,?,?,?)",
                                (int(words[1].strip()), words[2].replace("\n", "").strip(), 0, 0))
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
        database.commit()


if __name__ == '__main__':
    main()
