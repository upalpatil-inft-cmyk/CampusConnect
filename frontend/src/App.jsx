import React from 'react'
import { useEffect, useMemo, useState } from 'react';
import { Navigate, NavLink, Route, Routes, useNavigate } from 'react-router-dom';
import { Bell, BriefcaseBusiness, CalendarDays, Clock3, MapPin, CheckCircle2, ClipboardCheck, FileText, GraduationCap, LayoutDashboard, LogOut, UserRound, Users, Building2, ArrowUpRight, BookOpen, CircleAlert } from 'lucide-react';
import { api, login } from './api';

const role = () => localStorage.getItem('campusconnect_role');

function Protected({ children }) { return localStorage.getItem('campusconnect_token') ? children : <Navigate to="/login" replace />; }

function Login() {
  const nav = useNavigate();
  const [email,setEmail]=useState('student@campusconnect.local');
  const [password,setPassword]=useState('Student@123');
  const [error,setError]=useState(''); const [busy,setBusy]=useState(false);
  async function submit(e){e.preventDefault();setBusy(true);setError('');try{await login(email,password);nav('/')}catch{setError('Invalid email or password.')}finally{setBusy(false)}}
  return <main className="login-page"><div className="login-orb orb-one"/><div className="login-orb orb-two"/>
    <section className="login-card"><div className="brand-mark">C</div><p className="eyebrow">COLLEGE PORTAL</p><h1>CampusConnect</h1><p className="login-copy">A focused academic and placement workspace.</p>
      <form onSubmit={submit}><label>Email<input value={email} onChange={e=>setEmail(e.target.value)} autoComplete="username"/></label><label>Password<input type="password" value={password} onChange={e=>setPassword(e.target.value)} autoComplete="current-password"/></label>{error&&<div className="error">{error}</div>}<button className="primary wide" disabled={busy}>{busy?'Signing in…':'Sign in'} <ArrowUpRight size={15}/></button></form>
      <div className="demo-box"><b>Demo access</b><span>student@campusconnect.local · CampusConnect!7Qv#29Lm</span></div>
    </section>
  </main>
}

function Shell({children}){
  const nav=useNavigate(); const r=role(); const name=localStorage.getItem('campusconnect_name')||'User';
  const student=[['/','Overview',LayoutDashboard],['/profile','Profile',UserRound],['/timetable','Timetable',CalendarDays],['/subjects','Subjects',BookOpen],['/attendance','Attendance',ClipboardCheck],['/marks','Marks',GraduationCap],['/assignments','Assignments',FileText],['/placements','Placements',BriefcaseBusiness]];
  const faculty=[['/','Overview',LayoutDashboard],['/faculty/attendance','Attendance',ClipboardCheck],['/faculty/marks','Marks',GraduationCap],['/faculty/assignments','Assignments',FileText]];
  const admin=[['/','Overview',LayoutDashboard],['/admin/companies','Companies',Building2],['/admin/drives','Placement Drives',BriefcaseBusiness],['/admin/applications','Applications',Users]];
  const links=r==='STUDENT'?student:r==='FACULTY'?faculty:admin;
  function logout(){localStorage.clear();nav('/login')}
  return <div className="app-shell"><header className="topbar"><div className="brand"><span className="brand-mark">C</span><span><strong>CampusConnect</strong><small>College portal</small></span></div><nav className="topnav">{links.map(([to,label,Icon])=><NavLink key={to} to={to} end={to==='/' }><Icon size={15}/>{label}</NavLink>)}</nav><div className="top-actions"><button className="icon-btn" title="Notifications"><Bell size={17}/></button><div className="user-chip"><div className="avatar">{name[0]}</div><div><b>{name}</b><small>{r}</small></div></div><button className="logout-btn" onClick={logout}><LogOut size={15}/></button></div></header><main className="content">{children}</main></div>
}

function Page({eyebrow='CAMPUSCONNECT',title,subtitle,action,children}){return <><div className="page-head"><div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1>{subtitle&&<p className="subtitle">{subtitle}</p>}</div>{action}</div>{children}</>}
function Panel({title,meta,children,className=''}){return <section className={'panel '+className}><div className="panel-head"><div><h2>{title}</h2>{meta&&<p>{meta}</p>}</div></div>{children}</section>}
function Stat({label,value,note,accent='green',icon:Icon}){return <article className="stat-card"><div className="stat-top"><span className="label">{label}</span>{Icon&&<span className={'stat-icon '+accent}><Icon size={16}/></span>}</div><strong>{value}</strong><small>{note}</small></article>}
function Loading(){return <div className="loading">Loading your campus data…</div>}

function StudentDashboard(){
 const [me,setMe]=useState(null),[notices,setNotices]=useState([]),[marks,setMarks]=useState([]),[assignments,setAssignments]=useState([]),[attendance,setAttendance]=useState([]);
 useEffect(()=>{Promise.all([api.get('/student/me'),api.get('/notices'),api.get('/student/marks'),api.get('/assignments'),api.get('/student/attendance')]).then(([a,b,c,d,e])=>{setMe(a.data);setNotices(b.data);setMarks(c.data);setAssignments(d.data);setAttendance(e.data)}).catch(()=>{})},[]);
 const attendancePct=useMemo(()=>{if(!attendance.length)return '—';const p=attendance.filter(x=>x.present).length;return Math.round(p/attendance.length*100)+'%'},[attendance]);
 return <Page eyebrow="STUDENT DASHBOARD" title={`Good morning, ${me?.name?.split(' ')[0]||'Upal'}.`} subtitle="Everything important for your semester, in one place." action={<div className="semester">Semester {me?.semester??5}<i>•</i>{me?.department||'B.Tech INFT'}</div>}>
  <div className="bento">
   <Stat label="ATTENDANCE" value={attendancePct} note="Across recorded classes" icon={CheckCircle2}/>
   <Stat label="CURRENT CGPA" value={me?.cgpa??'—'} note="Latest academic record" accent="orange" icon={GraduationCap}/>
   <Stat label="ASSIGNMENTS" value={assignments.length||'0'} note="Active assignments" accent="cream" icon={FileText}/>
   <Panel title="Academic overview" meta="Latest subject records" className="academic-panel"><div className="subject-table">{marks.length?marks.slice(0,5).map((m,i)=><div className="subject-row" key={m.id||i}><div><b>{m.subject?.name||m.subject||m.subjectName||'Subject'}</b><small>Internal assessment</small></div><strong>{m.internalMarks??'—'}</strong><span>{m.totalMarks?`${m.internalMarks}/${m.totalMarks}`:'—'}</span></div>):<div className="empty">No marks recorded yet.</div>}</div></Panel>
   <Panel title="Placement pulse" meta="Current opportunities" className="placement-panel"><div className="green-panel"><div className="panel-number">03</div><h3>Companies accepting applications.</h3><p>Explore open drives, eligibility and deadlines from the placements section.</p><NavLink to="/placements">Explore drives <ArrowUpRight size={14}/></NavLink></div></Panel>
   <Panel title="Recent notices" meta="College updates" className="notice-panel"><div>{notices.slice(0,3).map(n=><div className="notice-row" key={n.id}><span className="notice-dot"/><div><b>{n.title}</b><small>{n.content}</small></div></div>)}{!notices.length&&<div className="empty">No notices available.</div>}</div></Panel>
   <Panel title="Upcoming work" meta="Your current assignments" className="work-panel"><div>{assignments.slice(0,3).map((a,i)=><div className="work-row" key={a.id||i}><div className="work-icon"><CalendarDays size={15}/></div><div><b>{a.title}</b><small>{a.deadline||'No deadline'}</small></div></div>)}{!assignments.length&&<div className="empty">No active assignments.</div>}</div></Panel>
  </div>
 </Page>
}

function Profile(){const[me,setMe]=useState(null),[form,setForm]=useState({fullName:'',phone:'',githubUrl:'',linkedinUrl:''}),[busy,setBusy]=useState(false);useEffect(()=>{api.get('/student/me').then(r=>{setMe(r.data);setForm({fullName:r.data.name||'',phone:r.data.phone||'',githubUrl:r.data.githubUrl||'',linkedinUrl:r.data.linkedinUrl||''})})},[]);async function save(e){e.preventDefault();setBusy(true);try{const r=await api.put('/student/me',form);setMe(r.data);localStorage.setItem('campusconnect_name',r.data.name);alert('Profile updated.')}catch(e){alert(e.response?.data?.message||'Unable to update profile')}finally{setBusy(false)}}return <Page title="My profile" subtitle="Keep your academic identity and contact details up to date."><div className="profile-grid"><Panel title="Academic identity"><div className="detail-grid">{[['Roll number',me?.rollNumber],['Department',me?.department],['Semester',me?.semester],['CGPA',me?.cgpa],['Email',me?.email]].map(([k,v])=><div key={k}><span>{k}</span><strong>{v||'—'}</strong></div>)}</div></Panel><Panel title="Contact details"><form onSubmit={save}><label>Full name<input required value={form.fullName} onChange={e=>setForm({...form,fullName:e.target.value})}/></label><label>Phone<input value={form.phone} onChange={e=>setForm({...form,phone:e.target.value})}/></label><label>GitHub URL<input value={form.githubUrl} onChange={e=>setForm({...form,githubUrl:e.target.value})}/></label><label>LinkedIn URL<input value={form.linkedinUrl} onChange={e=>setForm({...form,linkedinUrl:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Saving…':'Save profile'}</button></form></Panel></div></Page>}
function Attendance(){const[rows,setRows]=useState([]);useEffect(()=>{api.get('/student/attendance').then(r=>setRows(r.data))},[]);const present=rows.filter(x=>x.present).length;return <Page title="Attendance" subtitle="Subject-wise attendance records."><div className="stat-grid"><Stat label="RECORDED CLASSES" value={rows.length} note="Available records"/><Stat label="PRESENT" value={present} note="Classes attended"/></div><DataTable cols={['subject','date','present']} rows={rows}/></Page>}
function Marks(){const[rows,setRows]=useState([]);useEffect(()=>{api.get('/student/marks').then(r=>setRows(r.data))},[]);return <Page title="Marks" subtitle="Your internal assessment performance."><DataTable cols={['subject','internalMarks','totalMarks']} rows={rows}/></Page>}
function Assignments(){
 const[rows,setRows]=useState([]),[submissions,setSubmissions]=useState([]),[file,setFile]=useState(''),[busy,setBusy]=useState(false);
 async function load(){const[a,b]=await Promise.all([api.get('/assignments'),api.get('/student/submissions')]);setRows(a.data);setSubmissions(b.data)}
 useEffect(()=>{load()},[]);
 const submitted=new Set(submissions.map(x=>x.assignment));
 async function submit(id){if(!file.trim())return alert('Enter a file name for this prototype submission.');setBusy(true);try{await api.post('/assignments/submit',{assignmentId:id,fileName:file.trim()});setFile('');await load()}catch(e){alert(e.response?.data?.message||'Unable to submit assignment')}finally{setBusy(false)}}
 return <Page title="Assignments" subtitle="Tasks published by your faculty and your submission status.">
  <div className="assignment-list">{rows.map(a=><article className="assignment-item" key={a.id}>
   <div><span className="label">ASSIGNMENT</span><h3>{a.title}</h3><p>{a.description}</p><small>Deadline · {a.deadline||'Not set'}</small></div>
   <div className="assignment-action">{submitted.has(a.title)?<span className="status-ok"><CheckCircle2 size={15}/> Submitted</span>:<><input value={file} onChange={e=>setFile(e.target.value)} placeholder="filename.pdf"/><button className="primary" disabled={busy} onClick={()=>submit(a.id)}>{busy?'Saving…':'Submit'}</button></>}</div>
  </article>)}{!rows.length&&<div className="panel empty">No assignments have been published yet.</div>}</div>
  <Panel title="Submission history"><DataTable cols={['assignment','fileName','submittedAt','marks','feedback']} rows={submissions}/></Panel>
 </Page>
}
function Placements(){const[rows,setRows]=useState([]),[apps,setApps]=useState([]);async function load(){const[a,b]=await Promise.all([api.get('/placements/drives'),api.get('/student/applications')]);setRows(a.data);setApps(b.data)}useEffect(()=>{load()},[]);async function apply(id){try{await api.post(`/placements/drives/${id}/apply`);load()}catch(e){alert(e.response?.data?.message||'Unable to apply')}}return <Page title="Placements" subtitle="Discover eligible opportunities and track your applications."><div className="cards">{rows.map(d=><article className="op-card" key={d.id}><span className="label">OPEN DRIVE</span><h3>{d.company?.name}</h3><p>{d.jobRole}</p><strong>₹{d.packageLpa} LPA</strong><small>Minimum CGPA {d.minimumCgpa}</small><button className="primary" onClick={()=>apply(d.id)}>Apply <ArrowUpRight size={14}/></button></article>)}</div><Panel title="My applications"><DataTable cols={['company','role','status']} rows={apps}/></Panel></Page>}

function FacultyDashboard(){return <Page eyebrow="FACULTY DASHBOARD" title="Good morning." subtitle="Keep your classes moving without unnecessary admin overhead."><div className="stat-grid"><Stat label="ASSIGNED CLASSES" value="2" note="Current term"/><Stat label="STUDENTS" value="64" note="Across your classes"/><Stat label="ASSIGNMENTS" value="1" note="Active"/><Stat label="PENDING REVIEW" value="0" note="Submissions"/></div><Panel title="Next actions"><div className="action-list"><NavLink to="/faculty/attendance"><ClipboardCheck size={16}/>Take today's attendance<ArrowUpRight size={15}/></NavLink><NavLink to="/faculty/assignments"><FileText size={16}/>Publish an assignment<ArrowUpRight size={15}/></NavLink></div></Panel></Page>}
function FacultyAttendance(){const[form,setForm]=useState({studentId:1,subjectId:1,date:new Date().toISOString().slice(0,10),present:true});async function save(e){e.preventDefault();await api.post('/faculty/attendance',form);alert('Attendance saved.')}return <Page title="Mark attendance" subtitle="Record attendance for a student and subject."><Panel title="Attendance entry"><form className="form-grid" onSubmit={save}><label>Student ID<input value={form.studentId} onChange={e=>setForm({...form,studentId:e.target.value})}/></label><label>Subject ID<input value={form.subjectId} onChange={e=>setForm({...form,subjectId:e.target.value})}/></label><label>Date<input type="date" value={form.date} onChange={e=>setForm({...form,date:e.target.value})}/></label><label>Status<select value={form.present} onChange={e=>setForm({...form,present:e.target.value==='true'})}><option value="true">Present</option><option value="false">Absent</option></select></label><button className="primary">Save attendance</button></form></Panel></Page>}
function FacultyMarks(){const[form,setForm]=useState({studentId:1,subjectId:1,internalMarks:'',totalMarks:50});const[busy,setBusy]=useState(false);async function save(e){e.preventDefault();setBusy(true);try{await api.post('/faculty/marks',form);alert('Marks saved.');setForm({...form,internalMarks:''})}catch(e){alert(e.response?.data?.message||'Unable to save marks')}finally{setBusy(false)}}return <Page title="Record marks" subtitle="Add internal assessment marks for a student."><Panel title="Marks entry"><form className="form-grid" onSubmit={save}><label>Student ID<input required type="number" value={form.studentId} onChange={e=>setForm({...form,studentId:e.target.value})}/></label><label>Subject ID<input required type="number" value={form.subjectId} onChange={e=>setForm({...form,subjectId:e.target.value})}/></label><label>Marks obtained<input required type="number" min="0" value={form.internalMarks} onChange={e=>setForm({...form,internalMarks:e.target.value})}/></label><label>Total marks<input required type="number" min="1" value={form.totalMarks} onChange={e=>setForm({...form,totalMarks:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Saving…':'Save marks'}</button></form></Panel></Page>}
function FacultyAssignments(){const[form,setForm]=useState({title:'',description:'',subjectId:1,deadline:''});const[busy,setBusy]=useState(false);async function save(e){e.preventDefault();setBusy(true);try{await api.post('/faculty/assignments',form);alert('Assignment published.');setForm({title:'',description:'',subjectId:1,deadline:''})}catch(e){alert(e.response?.data?.message||'Unable to publish assignment')}finally{setBusy(false)}}return <Page title="Create assignment" subtitle="Publish a clear task with a deadline."><Panel title="New assignment"><form onSubmit={save}><label>Title<input required value={form.title} onChange={e=>setForm({...form,title:e.target.value})}/></label><label>Description<textarea required value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label><label>Subject ID<input required type="number" value={form.subjectId} onChange={e=>setForm({...form,subjectId:e.target.value})}/></label><label>Deadline<input required type="date" value={form.deadline} onChange={e=>setForm({...form,deadline:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Publishing…':'Publish assignment'}</button></form></Panel></Page>}
function AdminDashboard(){return <Page eyebrow="ADMIN DASHBOARD" title="System overview" subtitle="A restrained control surface for campus operations."><div className="stat-grid"><Stat label="STUDENTS" value="1" note="Registered"/><Stat label="FACULTY" value="1" note="Active accounts"/><Stat label="OPEN DRIVES" value="1" note="Current term"/><Stat label="APPLICATIONS" value="0" note="Placement activity"/></div><Panel title="System status"><div className="status-ok"><CheckCircle2 size={16}/> API connected</div></Panel></Page>}
function AdminCompanies(){const[form,setForm]=useState({name:'',website:'',description:''});const[busy,setBusy]=useState(false);async function save(e){e.preventDefault();setBusy(true);try{await api.post('/admin/companies',form);alert('Company created.');setForm({name:'',website:'',description:''})}catch(e){alert(e.response?.data?.message||'Unable to create company')}finally{setBusy(false)}}return <Page title="Companies" subtitle="Add organizations for placement drives."><Panel title="New company"><form onSubmit={save}>{['name','website','description'].map(k=><label key={k}>{k}<input required={k==='name'} value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/></label>)}<button className="primary" disabled={busy}>{busy?'Saving…':'Add company'}</button></form></Panel></Page>}
function AdminDrives(){const[form,setForm]=useState({companyId:1,jobRole:'',packageLpa:'',minimumCgpa:'',deadline:'',eligibleBranches:'CS,IT'});const[busy,setBusy]=useState(false);async function save(e){e.preventDefault();setBusy(true);try{await api.post('/admin/drives',form);alert('Placement drive created.');setForm({...form,jobRole:'',packageLpa:'',minimumCgpa:'',deadline:''})}catch(e){alert(e.response?.data?.message||'Unable to create drive')}finally{setBusy(false)}}return <Page title="Placement drives" subtitle="Create opportunities students can discover and apply to."><Panel title="New placement drive"><form className="form-grid" onSubmit={save}><label>Company ID<input required type="number" value={form.companyId} onChange={e=>setForm({...form,companyId:e.target.value})}/></label><label>Job role<input required value={form.jobRole} onChange={e=>setForm({...form,jobRole:e.target.value})}/></label><label>Package (LPA)<input required type="number" step="0.1" value={form.packageLpa} onChange={e=>setForm({...form,packageLpa:e.target.value})}/></label><label>Minimum CGPA<input required type="number" step="0.1" value={form.minimumCgpa} onChange={e=>setForm({...form,minimumCgpa:e.target.value})}/></label><label>Deadline<input required type="date" value={form.deadline} onChange={e=>setForm({...form,deadline:e.target.value})}/></label><label>Eligible branches<input value={form.eligibleBranches} onChange={e=>setForm({...form,eligibleBranches:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Creating…':'Create drive'}</button></form></Panel></Page>}
function AdminApplications(){const[rows,setRows]=useState([]);useEffect(()=>{api.get('/admin/applications').then(r=>setRows(r.data))},[]);return <Page title="Applications" subtitle="Review student placement applications."><DataTable cols={['id','status','appliedAt']} rows={rows}/></Page>}

function Timetable(){
 const[rows,setRows]=useState([]),[day,setDay]=useState('ALL');
 useEffect(()=>{api.get('/student/timetable').then(r=>setRows(r.data)).catch(()=>setRows([]))},[]);
 const days=['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
 const visible=day==='ALL'?rows:rows.filter(x=>x.day===day);
 return <Page title="My timetable" subtitle="Your weekly class schedule, synced from the campus portal." action={<div className="semester">Semester 5 <i>•</i> Weekly view</div>}>
   <div className="day-tabs"><button className={day==='ALL'?'active':''} onClick={()=>setDay('ALL')}>All</button>{days.map(d=><button key={d} className={day===d?'active':''} onClick={()=>setDay(d)}>{d.slice(0,3)}</button>)}</div>
   <div className="timetable-grid">{visible.map(x=><article className="class-card" key={x.id}>
      <div className="class-time"><Clock3 size={15}/><b>{x.startTime.slice(0,5)} – {x.endTime.slice(0,5)}</b><span>{x.day}</span></div>
      <div className="class-main"><span className="label">{x.code}</span><h3>{x.subject}</h3><p>{x.faculty}</p></div>
      <div className="class-room"><MapPin size={14}/>{x.room}</div>
   </article>)}{!visible.length&&<div className="panel empty">No classes scheduled for this view.</div>}</div>
 </Page>
}

function Subjects(){
 const[rows,setRows]=useState([]);
 useEffect(()=>{api.get('/student/subjects').then(r=>setRows(r.data)).catch(()=>setRows([]))},[]);
 return <Page title="My subjects" subtitle="Academic snapshot for your current semester.">
   <div className="subject-cards">{rows.map(s=><article className="subject-card" key={s.id}><span className="label">{s.code}</span><h3>{s.name}</h3><div className="subject-meta"><span>Credits <b>{s.credits}</b></span><span>Attendance <b>{s.attendance}%</b></span><span>Marks <b>{s.internalMarks}/{s.totalMarks||'—'}</b></span></div></article>)}{!rows.length&&<div className="panel empty">No subjects available.</div>}</div>
 </Page>
}

function DataTable({cols,rows}){if(!rows?.length)return <div className="panel empty">No records yet.</div>;return <div className="table-wrap"><table><thead><tr>{cols.map(c=><th key={c}>{c.replace(/([A-Z])/g,' $1')}</th>)}</tr></thead><tbody>{rows.map((r,i)=><tr key={r.id||i}>{cols.map(c=><td key={c}>{typeof r[c]==='object'?JSON.stringify(r[c]):String(r[c]??'—')}</td>)}</tr>)}</tbody></table></div>}

export default function App(){
  const r=role();
  const protectedPage = (element) => <Protected><Shell>{element}</Shell></Protected>;
  return <Routes>
    <Route path="/login" element={r ? <Navigate to="/" replace/> : <Login/>}/>

    {r === 'STUDENT' && <>
      <Route path="/" element={protectedPage(<StudentDashboard/>)}/>
      <Route path="/profile" element={protectedPage(<Profile/>)}/><Route path="/timetable" element={protectedPage(<Timetable/>)}/><Route path="/subjects" element={protectedPage(<Subjects/>)}/>
      <Route path="/attendance" element={protectedPage(<Attendance/>)}/>
      <Route path="/marks" element={protectedPage(<Marks/>)}/>
      <Route path="/assignments" element={protectedPage(<Assignments/>)}/>
      <Route path="/placements" element={protectedPage(<Placements/>)}/>
    </>}

    {r === 'FACULTY' && <>
      <Route path="/" element={protectedPage(<FacultyDashboard/>)}/>
      <Route path="/faculty/attendance" element={protectedPage(<FacultyAttendance/>)}/>
      <Route path="/faculty/marks" element={protectedPage(<FacultyMarks/>)}/>
      <Route path="/faculty/assignments" element={protectedPage(<FacultyAssignments/>)}/>
    </>}

    {r === 'ADMIN' && <>
      <Route path="/" element={protectedPage(<AdminDashboard/>)}/>
      <Route path="/admin/companies" element={protectedPage(<AdminCompanies/>)}/>
      <Route path="/admin/drives" element={protectedPage(<AdminDrives/>)}/>
      <Route path="/admin/applications" element={protectedPage(<AdminApplications/>)}/>
    </>}

    <Route path="*" element={<Navigate to={r ? "/" : "/login"} replace/>}/>
  </Routes>
}
