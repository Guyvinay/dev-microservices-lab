import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TitleStrategy } from '@angular/router';
import { Student } from '../interfaces/student';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.css'
})
export class FormComponent implements OnInit {

  constructor(private formBuilder: FormBuilder) {}

  students:Student[] = [];

  ngOnInit(): void {
    console.log(this.myForm.value);
  }

  techStacks = [
    "Java",
    "Typescript",
    "Javascript",
    "Python",
    "Angular",
    "React",
    "Vue",
    "DJango"
  ]

  selectedTechStacks:string[] = [];

  onSubmit(): void {
    this.students.push(this.myForm.value);
    console.log(this.students);
  }

  myForm: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    profile: new FormControl('', Validators.required),
    techStacks: new FormControl([], Validators.required)
  });

  addTechStacks(tech: string) {
    const techStacksControl = this.myForm.get('techStacks') as FormControl;

    // Get the current value of techStacks
    const currentTechStacks = techStacksControl.value as string[];
    // Check if the selected tech is not already added
    if (!currentTechStacks.includes(tech)) {
      // Push the selected tech into the techStacks array
      currentTechStacks.push(tech);
      // Update the value of the techStacks form control
      techStacksControl.setValue(currentTechStacks);
      // this.selectedTechStacks = currentTechStacks;
    }
    this.selectedTechStacks = currentTechStacks;
  }

  removeSelectedTech(ind:number){
    this.selectedTechStacks.splice(ind, 1);
    console.log(this.selectedTechStacks);
  }

  get getName() {
    return this.myForm.get("name");
  }

  get getEmail() {
    return this.myForm.get("email");
  }
  
  get getTechStacks() {
    return this.myForm.get("techStacks");
  }

  deleteStudent(student:Student):void{

  }

  updateStudent(student:Student):void{

  }

}