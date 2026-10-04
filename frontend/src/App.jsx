import React from 'react'
import { useEffect, useMemo, useState } from 'react';
import { Navigate, NavLink, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { Bell, BriefcaseBusiness, CalendarDays, Clock3, MapPin, CheckCircle2, ClipboardCheck, FileText, GraduationCap, LayoutDashboard, LogOut, UserRound, Users, Building2, UserCheck, ArrowUpRight, BookOpen, CircleAlert, TrendingUp, ChevronDown, Inbox } from 'lucide-react';
import { api, login } from './api';

const role = () => localStorage.getItem('campusconnect_role');

function Protected({ children }) { return localStorage.getItem('campusconnect_token') ? children : <Navigate to="/login" replace />; }

function Login() {
  const nav = useNavigate();
  const [email,setEmail] = useState('');
  const [password,setPassword] = useState('');
  const [error,setError] = useState('');
  const [busy,setBusy] = useState(false);

  async function submit(e){
    e.preventDefault();
    setBusy(true);
    setError('');
    try{
      localStorage.clear();
      const data = await login(email.trim(), password);
      localStorage.setItem('campusconnect_email', email.trim());
      nav('/', {replace:true});
      window.setTimeout(() => window.location.reload(), 0);
    }catch{
      setError('Invalid email or password.');
    }finally{
      setBusy(false);
    }
  }

  return <div className="login-page">
    <div className="login-orb orb-one"/>
    <div className="login-orb orb-two"/>
    <section className="login-card">
      <div className="brand-mark">C</div>
      <p className="eyebrow">CAMPUSCONNECT</p>
      <h1>Welcome back.</h1>
      <p className="login-copy">Sign in to access your college portal.</p>
      <form onSubmit={submit}>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={e=>setEmail(e.target.value)}
            placeholder="you@campusconnect.local"
            autoComplete="username"
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={e=>setPassword(e.target.value)}
            placeholder="Enter your password"
            autoComplete="current-password"
            required
          />
        </label>
        {error && <div className="error">{error}</div>}
        <button className="primary wide" type="submit" disabled={busy}>
          {busy ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
      <div className="demo-box">
        <b>CampusConnect Portal</b>
        Use your assigned student, faculty, or admin account.
      </div>
    </section>
  </div>;
}

function Shell({children}){
  const nav=useNavigate();
  const location=useLocation();
  const r=role();
  const name=localStorage.getItem('campusconnect_name')||'User';
  const email=localStorage.getItem('campusconnect_email')||(
    r==='FACULTY'?'faculty@campusconnect.local':
    r==='ADMIN'?'admin@campusconnect.local':'student@campusconnect.local'
  );
  const [notifications,setNotifications]=useState([]);
  const [unread,setUnread]=useState(0);
  const [noticeOpen,setNoticeOpen]=useState(false);
  const [profileOpen,setProfileOpen]=useState(false);
  const [academicOpen,setAcademicOpen]=useState(false);

  async function loadNotifications(){
    try{
      const res=await api.get('/notifications');
      setNotifications(Array.isArray(res.data?.items)?res.data.items:[]);
      setUnread(Number(res.data?.unread||0));
    }catch{
      setNotifications([]);
      setUnread(0);
    }
  }

  useEffect(()=>{loadNotifications()},[]);

  const studentAcademic=[
    ['/timetable','Timetable',CalendarDays],
    ['/subjects','Subjects',BookOpen],
    ['/performance','Performance',TrendingUp],
    ['/attendance','Attendance',ClipboardCheck],
    ['/marks','Marks',GraduationCap],
    ['/assignments','Assignments',FileText]
  ];
  const facultyAcademic=[
    ['/faculty/attendance','Attendance',ClipboardCheck],
    ['/faculty/marks','Marks',GraduationCap],
    ['/faculty/assignments','Assignments',FileText]
  ];
  const adminManagement=[
    ['/admin/users','Users',UserCheck],
    ['/admin/companies','Companies',Building2],
    ['/admin/drives','Placement Drives',BriefcaseBusiness]
  ];

  const studentTop=[['/','Overview',LayoutDashboard],['/placements','Placements',BriefcaseBusiness]];
  const facultyTop=[['/','Overview',LayoutDashboard],['/faculty/submissions','Review',CheckCircle2]];
  const adminTop=[['/','Overview',LayoutDashboard],['/admin/applications','Applications',Users]];
  const topLinks=r==='STUDENT'?studentTop:r==='FACULTY'?facultyTop:adminTop;
  const grouped=r==='STUDENT'?studentAcademic:r==='FACULTY'?facultyAcademic:adminManagement;
  const groupLabel=r==='ADMIN'?'Management':'Academics';
  const groupActive=grouped.some(([to])=>location.pathname===to);

  function closeMenus(){
    setNoticeOpen(false);
    setProfileOpen(false);
    setAcademicOpen(false);
  }

  async function openNotification(item){
    setNoticeOpen(false);
    if(!item.read){
      try{
        await api.patch('/notifications/'+item.id+'/read');
        setUnread(v=>Math.max(0,v-1));
        setNotifications(rows=>rows.map(n=>n.id===item.id?{...n,read:true}:n));
      }catch{}
    }
    if(item.link) nav(item.link);
  }

  async function markAllRead(){
    try{
      await api.post('/notifications/read-all');
      setNotifications(rows=>rows.map(n=>({...n,read:true})));
      setUnread(0);
    }catch{}
  }

  function logout(){
    localStorage.clear();
    window.location.replace('/login');
  }

  function goProfile(){
    setProfileOpen(false);
    if(r==='STUDENT') nav('/profile');
  }

  return <div className="app-shell">
    <header className="topbar">
      <div className="brand">
        <img className="brand-logo" src="/favicon.svg" alt="" aria-hidden="true" />
        <span><strong>CampusConnect</strong><small>College portal</small></span>
      </div>

      <nav className="topnav">
        {topLinks.map(([to,label,Icon])=><NavLink key={to} to={to} end={to==='/'} onClick={closeMenus}>
          <Icon size={15}/>{label}
        </NavLink>)}

        <div className="nav-dropdown-wrap">
          <button className={`nav-dropdown-trigger${groupActive?' active':''}`}
            onClick={()=>{setAcademicOpen(v=>!v);setNoticeOpen(false);setProfileOpen(false)}}>
            <BookOpen size={15}/>{groupLabel}<ChevronDown size={13} className={academicOpen?'chevron-open':''}/>
          </button>
          {academicOpen&&<div className="nav-dropdown">
            <div className="nav-dropdown-title">{groupLabel}</div>
            {grouped.map(([to,label,Icon])=><NavLink key={to} to={to} end onClick={closeMenus}>
              <Icon size={15}/><span>{label}</span>
            </NavLink>)}
          </div>}
        </div>
      </nav>

      <div className="top-actions">
        <div className="notification-wrap">
          <button className={`icon-btn notification-trigger${unread>0?' has-unread':''}`} title="Notifications"
            onClick={()=>{setNoticeOpen(v=>!v);setProfileOpen(false);setAcademicOpen(false)}}>
            <Bell size={17}/>
            {unread>0&&<span className="notification-count">{unread>9?'9+':unread}</span>}
          </button>
          {noticeOpen&&<div className="dropdown notifications-dropdown">
            <div className="dropdown-head">
              <div><b>Notifications</b><small>{unread>0?`${unread} unread update${unread===1?'':'s'}`:'You are all caught up'}</small></div>
              <div className="notification-head-actions">
                {unread>0&&<button className="mark-read-btn" onClick={markAllRead}>Mark all read</button>}
                <Bell size={15}/>
              </div>
            </div>
            <div className="notification-list">
              {notifications.length ? notifications.map(n=><button className={`notification-item${n.read?'':' unread'}`} key={n.id} onClick={()=>openNotification(n)}>
                <span className={`notification-bullet type-${String(n.type||'').toLowerCase()}`}/>
                <span className="notification-copy"><b>{n.title}</b><small>{n.message}</small><em>{n.createdAt?new Date(n.createdAt).toLocaleString([], {day:'2-digit',month:'short',hour:'2-digit',minute:'2-digit'}):''}</em></span>
                {!n.read&&<span className="unread-dot"/>}
              </button>) : <div className="dropdown-empty">No notifications right now.</div>}
            </div>
          </div>}
        </div>

        <div className="profile-wrap">
          <button className="user-chip profile-trigger"
            onClick={()=>{setProfileOpen(v=>!v);setNoticeOpen(false);setAcademicOpen(false)}}>
            <div className="avatar">{name[0]}</div>
            <div><b>{name}</b><small>{r}</small></div>
            <ChevronDown size={14} className={profileOpen?'chevron-open':''}/>
          </button>
          {profileOpen&&<div className="dropdown profile-dropdown">
            <div className="profile-summary">
              <div className="avatar large">{name[0]}</div>
              <div><b>{name}</b><small>{email}</small><span>{r}</span></div>
            </div>
            {r==='STUDENT'&&<button className="dropdown-action" onClick={goProfile}><UserRound size={15}/>My profile</button>}
            <button className="dropdown-action" onClick={()=>{setProfileOpen(false);nav('/')}}><LayoutDashboard size={15}/>Dashboard</button>
            <button className="dropdown-action danger" onClick={logout}><LogOut size={15}/>Sign out</button>
          </div>}
        </div>
      </div>
    </header>
    <main className="content">{children}</main>
  </div>
}
function Page({eyebrow='CAMPUSCONNECT',title,subtitle,action,children}){return <><div className="page-head"><div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1>{subtitle&&<p className="subtitle">{subtitle}</p>}</div>{action}</div>{children}</>}
function Panel({title,meta,children,className=''}){return <section className={'panel '+className}><div className="panel-head"><div><h2>{title}</h2>{meta&&<p>{meta}</p>}</div></div>{children}</section>}
function Stat({label,value,note,accent='green',icon:Icon}){return <article className="stat-card"><div className="stat-top"><span className="label">{label}</span>{Icon&&<span className={'stat-icon '+accent}><Icon size={16}/></span>}</div><strong>{value}</strong><small>{note}</small></article>}
function Loading(){return <div className="loading">Loading your campus data…</div>}


function CampusNavigator({data,onNavigate}){
  const recommendations=Array.isArray(data?.recommendations)?data.recommendations:[];
  const score=Number(data?.readinessScore||0);
  const scoreLabel=score>=80?'Strong position':score>=60?'On track':'Needs attention';
  const priorityClass={HIGH:'high',MEDIUM:'medium',LOW:'low'};
  return <Panel title="Campus Navigator" meta="Your next best actions, based on your live campus data." className="navigator-panel">
    <div className="navigator-head">
      <div>
        <span className="label">PLACEMENT READINESS</span>
        <div className="readiness-score"><strong>{score}</strong><span>/100</span></div>
        <p>{scoreLabel} · {data?.summary||'Review the actions below.'}</p>
      </div>
      <div className="readiness-ring" style={{'--score':score}}><b>{score}</b><span>ready</span></div>
    </div>
    <div className="navigator-list">
      {recommendations.length ? recommendations.map((item,index)=><article className="navigator-item" key={item.type+'-'+item.title+'-'+index}>
        <div className={`navigator-priority ${priorityClass[item.priority]||'low'}`}>{item.priority==='HIGH'?'!':item.priority==='MEDIUM'?'•':'+'}</div>
        <div className="navigator-copy">
          <div className="navigator-title"><b>{item.title}</b><span>{item.type}</span></div>
          <p>{item.reason}</p>
          <small>{item.detail}</small>
        </div>
        <button className="navigator-action" onClick={()=>onNavigate(item.actionRoute)}>{item.actionLabel}<ArrowUpRight size={14}/></button>
      </article>) : <EmptyState icon={CheckCircle2} title="You're clear for now" message="No urgent assignments, placement actions or academic warnings were found."/>}
    </div>
  </Panel>
}

function StudentDashboard(){
 const nav=useNavigate();
 const [me,setMe]=useState(null),[notices,setNotices]=useState([]),[marks,setMarks]=useState([]),[assignments,setAssignments]=useState([]),[attendance,setAttendance]=useState([]),[placements,setPlacements]=useState([]),[analytics,setAnalytics]=useState(null),[navigator,setNavigator]=useState(null),[busy,setBusy]=useState(true),[loadError,setLoadError]=useState(false);
 useEffect(()=>{
   Promise.all([api.get('/student/me'),api.get('/notices'),api.get('/student/marks'),api.get('/assignments'),api.get('/student/attendance'),api.get('/placement-upgrades/student'),api.get('/student/analytics'),api.get('/student/navigator')]).then(([a,b,c,d,e,f,g,h])=>{
     setMe(a.data);setNotices(Array.isArray(b.data)?b.data:[]);setMarks(Array.isArray(c.data)?c.data:[]);setAssignments(Array.isArray(d.data)?d.data:[]);setAttendance(Array.isArray(e.data)?e.data:[]);setPlacements(Array.isArray(f.data)?f.data:[]);setAnalytics(g.data);setNavigator(h.data);
   }).catch(()=>setLoadError(true)).finally(()=>setBusy(false));
 },[]);
 const attendancePct=analytics?.attendancePercentage!=null?`${analytics.attendancePercentage}%`:attendance.length?`${Math.round(attendance.filter(x=>x.present).length/attendance.length*100)}%`:'—';
 const activeAssignments=assignments.filter(a=>a.deadline>=new Date().toISOString().slice(0,10));
 const eligibleDrives=placements.filter(x=>x.eligible).length;
 const greeting=(()=>{const h=new Date().getHours();return h<12?'Good morning':h<17?'Good afternoon':'Good evening'})();
 if(busy)return <Page eyebrow="STUDENT DASHBOARD" title="Loading your dashboard…" subtitle="Pulling together your latest campus data."><div className="dashboard-loading"><div className="loading-orb"/><b>Preparing your semester snapshot</b><span>Academics, assignments and opportunities are loading.</span></div></Page>;
 if(loadError && !me)return <Page eyebrow="STUDENT DASHBOARD" title="We couldn't load your dashboard." subtitle="Your session is still active. Try again in a moment."><div className="dashboard-error"><CircleAlert size={20}/><div><b>Campus data is temporarily unavailable.</b><span>Refresh the page to retry the dashboard request.</span></div><button className="secondary-btn" onClick={()=>window.location.reload()}>Retry</button></div></Page>;
 return <Page eyebrow="STUDENT DASHBOARD" title={`${greeting}, ${me?.name?.split(' ')[0]||'there'}.`} subtitle="Your semester at a glance — academics, work and opportunities." action={<div className="semester">Semester {me?.semester??5}<i>•</i>{me?.department||'B.Tech INFT'}</div>}>
  <div className="dashboard-hero"><div><span className="label">THIS SEMESTER</span><h2>Stay on top of your campus life.</h2><p>Track performance, finish assignments and catch the opportunities that match your profile.</p></div><div className="hero-metrics"><div><strong>{analytics?.cgpa??me?.cgpa??'—'}</strong><span>CGPA</span></div><div><strong>{attendancePct}</strong><span>Attendance</span></div><div><strong>{eligibleDrives}</strong><span>Eligible drives</span></div></div></div>
  <div className="dashboard-stats"><Stat label="ATTENDANCE" value={attendancePct} note="Across recorded classes" icon={CheckCircle2}/><Stat label="CURRENT CGPA" value={me?.cgpa??'—'} note="Latest academic record" accent="orange" icon={GraduationCap}/><Stat label="OPEN ASSIGNMENTS" value={activeAssignments.length} note="Deadlines still open" accent="cream" icon={FileText}/><Stat label="ELIGIBLE DRIVES" value={eligibleDrives} note="Based on your profile" icon={BriefcaseBusiness}/></div>
  <CampusNavigator data={navigator} onNavigate={route=>window.history.pushState({},'',route) || window.dispatchEvent(new PopStateEvent('popstate'))}/>
  <div className="dashboard-grid">
   <Panel title="Academic snapshot" meta="Latest subject records"><div className="subject-table">{marks.length?marks.slice(0,5).map((m,i)=><div className="subject-row" key={m.id||i}><div><b>{m.subject?.name||m.subject||m.subjectName||'Subject'}</b><small>Internal assessment</small></div><strong>{m.internalMarks??'—'}</strong><span>{m.totalMarks?`${m.internalMarks}/${m.totalMarks}`:'—'}</span></div>):<div className="empty">No marks recorded yet.</div>}</div><NavLink className="panel-link" to="/performance">View full performance <ArrowUpRight size={14}/></NavLink></Panel>
   <Panel title="Placement pulse" meta="Opportunities for you"><div className="placement-pulse"><div className="pulse-icon"><BriefcaseBusiness size={19}/></div><div><strong>{eligibleDrives} eligible drive{eligibleDrives===1?'':'s'}</strong><p>Review companies, packages and deadlines before applying.</p></div></div><NavLink className="panel-link" to="/placements">Explore placements <ArrowUpRight size={14}/></NavLink></Panel>
   <Panel title="Upcoming work" meta="Closest assignment deadlines"><div>{activeAssignments.slice().sort((a,b)=>String(a.deadline).localeCompare(String(b.deadline))).slice(0,3).map((a,i)=><div className="work-row" key={a.id||i}><div className="work-icon"><CalendarDays size={15}/></div><div><b>{a.title}</b><small>{a.subject?.name||a.subjectCode||'Assignment'} · Due {a.deadline||'No deadline'}</small></div></div>)}{!activeAssignments.length&&<div className="empty">No upcoming assignments.</div>}</div><NavLink className="panel-link" to="/assignments">Open assignments <ArrowUpRight size={14}/></NavLink></Panel>
   <Panel title="Recent notices" meta="Latest college updates"><div>{notices.slice(0,3).map(n=><div className="notice-row" key={n.id}><span className="notice-dot"/><div><b>{n.title}</b><small>{n.content}</small></div></div>)}{!notices.length&&<div className="empty">No notices available.</div>}</div></Panel>
  </div>
 </Page>
}

function Profile(){const[me,setMe]=useState(null),[form,setForm]=useState({fullName:'',phone:'',githubUrl:'',linkedinUrl:''}),[busy,setBusy]=useState(false),[message,setMessage]=useState(null);useEffect(()=>{api.get('/student/me').then(r=>{setMe(r.data);setForm({fullName:r.data.name||'',phone:r.data.phone||'',githubUrl:r.data.githubUrl||'',linkedinUrl:r.data.linkedinUrl||''})})},[]);async function save(e){e.preventDefault();setMessage(null);setBusy(true);try{const r=await api.put('/student/me',form);setMe(r.data);localStorage.setItem('campusconnect_name',r.data.name);setMessage({type:'success',text:'Profile updated successfully.'})}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to update profile'})}finally{setBusy(false)}}return <Page title="My profile" subtitle="Keep your academic identity and contact details up to date."><div className="profile-grid"><Panel title="Academic identity"><div className="detail-grid">{[['Roll number',me?.rollNumber],['Department',me?.department],['Semester',me?.semester],['CGPA',me?.cgpa],['Email',me?.email]].map(([k,v])=><div key={k}><span>{k}</span><strong>{v||'—'}</strong></div>)}</div></Panel><Panel title="Contact details"><form onSubmit={save}><label>Full name<input required value={form.fullName} onChange={e=>setForm({...form,fullName:e.target.value})}/></label><label>Phone<input value={form.phone} onChange={e=>setForm({...form,phone:e.target.value})}/></label><label>GitHub URL<input value={form.githubUrl} onChange={e=>setForm({...form,githubUrl:e.target.value})}/></label><label>LinkedIn URL<input value={form.linkedinUrl} onChange={e=>setForm({...form,linkedinUrl:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Saving…':'Save profile'}</button>{message&&<FormMessage type={message.type} message={message.text}/>}</form></Panel></div></Page>}
function Attendance(){const[rows,setRows]=useState([]);useEffect(()=>{api.get('/student/attendance').then(r=>setRows(r.data))},[]);const present=rows.filter(x=>x.present).length;return <Page title="Attendance" subtitle="Subject-wise attendance records."><div className="stat-grid"><Stat label="RECORDED CLASSES" value={rows.length} note="Available records"/><Stat label="PRESENT" value={present} note="Classes attended"/></div><DataTable cols={['subject','date','present']} rows={rows}/></Page>}
function Marks(){const[rows,setRows]=useState([]);useEffect(()=>{api.get('/student/marks').then(r=>setRows(r.data))},[]);return <Page title="Marks" subtitle="Your internal assessment performance."><DataTable cols={['subject','internalMarks','totalMarks']} rows={rows}/></Page>}
function Performance(){
 const[data,setData]=useState(null),[busy,setBusy]=useState(true);
 useEffect(()=>{api.get('/student/analytics').then(r=>setData(r.data)).catch(()=>setData(null)).finally(()=>setBusy(false))},[]);
 if(busy)return <Page title="Academic performance" subtitle="Your current semester performance at a glance."><Loading/></Page>;
 if(!data)return <Page title="Academic performance" subtitle="Your current semester performance at a glance."><div className="panel empty">Unable to load academic analytics.</div></Page>;
 return <Page title="Academic performance" subtitle={`Semester ${data.semester} · Marks, attendance and subject trends`} action={<div className="semester">Semester {data.semester} <i>•</i> Live data</div>}>
   <div className="stat-grid performance-stats">
     <Stat label="CURRENT CGPA" value={data.cgpa??'—'} note="Academic record" icon={GraduationCap}/>
     <Stat label="MARKS AVERAGE" value={`${data.marksPercentage}%`} note={`${data.totalMarks}/${data.possibleMarks} marks`} accent="orange" icon={TrendingUp}/>
     <Stat label="ATTENDANCE" value={`${data.attendancePercentage}%`} note={`${data.presentClasses}/${data.recordedClasses} classes present`} icon={CheckCircle2}/>
     <Stat label="SUBJECTS" value={data.subjects} note="Current semester" accent="cream" icon={BookOpen}/>
   </div>
   <div className="performance-grid">
     <Panel title="Subject performance" meta="Internal assessment percentage">
       <div className="performance-list">{data.subjectPerformance.map(s=><div className="performance-row" key={s.id}>
         <div className="performance-heading"><div><span className="label">{s.code}</span><h3>{s.name}</h3></div><strong>{s.percentage}%</strong></div>
         <div className="progress-track"><span style={{width:`${Math.min(100,s.percentage)}%`}}/></div>
         <div className="performance-meta"><span>{s.marks}/{s.totalMarks||'—'} marks</span><span>Grade <b>{s.grade}</b></span><span>Attendance <b>{s.attendance}%</b></span></div>
       </div>)}{!data.subjectPerformance.length&&<div className="empty">No marks recorded for this semester yet.</div>}</div>
     </Panel>
     <Panel title="Performance breakdown" meta="Quick read of the current semester">
       <div className="breakdown">
         <div className="breakdown-item"><span>Marks</span><strong>{data.marksPercentage}%</strong><div className="progress-track"><span style={{width:`${Math.min(100,data.marksPercentage)}%`}}/></div></div>
         <div className="breakdown-item"><span>Attendance</span><strong>{data.attendancePercentage}%</strong><div className="progress-track orange-track"><span style={{width:`${Math.min(100,data.attendancePercentage)}%`}}/></div></div>
         <div className="performance-note"><CircleAlert size={16}/><p>Percentages are calculated from the marks and attendance records currently stored in CampusConnect.</p></div>
       </div>
     </Panel>
   </div>
 </Page>
}

function Assignments(){
 const[rows,setRows]=useState([]),[submissions,setSubmissions]=useState([]),[filter,setFilter]=useState('ALL'),[file,setFile]=useState(null),[busy,setBusy]=useState(false),[message,setMessage]=useState(null);
 async function load(){const[a,b]=await Promise.all([api.get('/assignments'),api.get('/assignments/student-submissions')]);setRows(a.data);setSubmissions(b.data)}
 useEffect(()=>{load()},[]);
 const submitted=new Map(submissions.map(x=>[x.assignmentId,x]));
 const today=new Date().toISOString().slice(0,10);
 const visible=rows.filter(a=>{
   const status=submitted.has(a.id)?'SUBMITTED':(a.deadline<today?'OVERDUE':'PENDING');
   return filter==='ALL'||status===filter;
 });
 async function submit(id){
   if(!file){setMessage({type:'error',text:'Choose a file before submitting.'});return;}
   setBusy(true);
   try{
     const form=new FormData(); form.append('assignmentId',id); form.append('file',file);
     await api.post('/assignments/submit-file',form);
     setFile(null); await load();
   }catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to submit assignment'})}finally{setBusy(false)}
 }
 function due(deadline){if(deadline<today)return 'Overdue'; const days=Math.ceil((new Date(deadline)-new Date(today))/86400000); return days===0?'Due today':`Due in ${days}d`}
 return <Page title="Assignments" subtitle="Tasks published by your faculty, with submission status and feedback.">
  {message&&<FormMessage type={message.type} message={message.text}/>} 
  <div className="day-tabs assignment-filters">{['ALL','PENDING','SUBMITTED','OVERDUE'].map(x=><button key={x} className={filter===x?'active':''} onClick={()=>setFilter(x)}>{x[0]+x.slice(1).toLowerCase()}</button>)}</div>
  {!visible.length?<EmptyState icon={FileText} title={filter==='ALL'?'No assignments yet':`No ${filter.toLowerCase()} assignments`} message={filter==='ALL'?'Your faculty assignments will appear here.':'Try another filter to see other assignment states.'}/>:<div className="assignment-list">{visible.map(a=>{
    const s=submitted.get(a.id); const status=s?'SUBMITTED':(a.deadline<today?'OVERDUE':'PENDING');
    return <article className="assignment-item" key={a.id}>
      <div><span className="label">{a.subject?.code||'ASSIGNMENT'}</span><h3>{a.title}</h3><p>{a.description}</p><small>Deadline · {a.deadline||'Not set'} · <b>{due(a.deadline)}</b></small>{s&&<div className="submission-note"><CheckCircle2 size={14}/> {s.fileName} · {s.marks==='—'?'Awaiting grade':`Marks: ${s.marks}`}{s.feedback!=='—'&&` · ${s.feedback}`}</div>}</div>
      <div className="assignment-action">{s?<span className="status-ok"><CheckCircle2 size={15}/> Submitted</span>:<><input key={file ? file.name : `empty-${a.id}`} id={`assignment-file-${a.id}`} type="file" onChange={e=>setFile(e.target.files?.[0]||null)}/><button className="primary" disabled={busy||status==='OVERDUE'} onClick={()=>submit(a.id)}>{busy?'Uploading…':'Submit file'}</button></>}</div>
    </article>
  })}</div>}
  <Panel title="Submission history" meta="Grades and faculty feedback appear here."><DataTable cols={['assignment','fileName','submittedAt','marks','feedback']} rows={submissions}/></Panel>
 </Page>
}
function Placements(){
 const[rows,setRows]=useState([]),[apps,setApps]=useState([]),[history,setHistory]=useState([]),[stats,setStats]=useState(null),[filter,setFilter]=useState('ALL'),[applyMessage,setApplyMessage]=useState(null);
 async function load(){
   const[a,b,c,d]=await Promise.all([api.get('/placement-upgrades/student'),api.get('/student/applications'),api.get('/placement-upgrades/history'),api.get('/placement-upgrades/stats')]);
   setRows(a.data);setApps(b.data);setHistory(c.data);setStats(d.data);
 }
 useEffect(()=>{load()},[]);
 async function apply(id){
   try{await api.post(`/placements/drives/${id}/apply`);load()}
   catch(e){setApplyMessage({type:'error',text:e.response?.data?.message||'Unable to apply'})}
 }
 const visible=rows.filter(d=>filter==='ALL'||(filter==='ELIGIBLE'&&d.eligible)||(filter==='APPLIED'&&d.applicationStatus));
 return <Page title="Placements" subtitle="Compare opportunities, understand eligibility, and track every application.">
   <div className="stat-grid placement-stats">
     <Stat label="OPEN DRIVES" value={stats?.openDrives??'—'} note="Current opportunities" icon={BriefcaseBusiness}/>
     <Stat label="MY APPLICATIONS" value={stats?.applications??'—'} note="Submitted applications" accent="orange" icon={FileText}/>
     <Stat label="ACTIVE" value={stats?.activeApplications??'—'} note="Awaiting an outcome" icon={Clock3}/>
     <Stat label="SELECTED" value={stats?.selected??'—'} note="Placement history" accent="cream" icon={CheckCircle2}/>
   </div>
   <div className="day-tabs placement-filters">{['ALL','ELIGIBLE','APPLIED'].map(x=><button key={x} className={filter===x?'active':''} onClick={()=>setFilter(x)}>{x[0]+x.slice(1).toLowerCase()}</button>)}</div>
   {applyMessage&&<FormMessage type={applyMessage.type} message={applyMessage.text}/>} 
   {!visible.length?<EmptyState icon={BriefcaseBusiness} title={filter==='ALL'?'No placement drives yet':`No ${filter.toLowerCase()} opportunities`} message="New campus opportunities will appear here when they are published."/>:<div className="cards">{visible.map(d=><article className="op-card placement-card" key={d.id}>
     <div className="placement-company"><span className="label">COMPANY</span><h3>{d.company}</h3></div>
     <p>{d.jobRole}</p><strong>₹{d.packageLpa} LPA</strong>
     <div className="eligibility-box"><b>Eligibility</b><span className={d.cgpaEligible?'ok':'bad'}>{d.cgpaEligible?'✓':'×'} CGPA {d.minimumCgpa}</span><span className={d.branchEligible?'ok':'bad'}>{d.branchEligible?'✓':'×'} {d.eligibleBranches||'All branches'}</span></div>
     <small>Deadline · {d.deadline}</small>
     {d.applicationStatus?<div className="status-ok"><CheckCircle2 size={15}/> {d.applicationStatus}</div>:<button className="primary" disabled={!d.eligible} onClick={()=>apply(d.id)}>{d.eligible?'Apply now':'Not eligible'} <ArrowUpRight size={14}/></button>}
   </article>)}</div>}
   <div className="profile-grid placement-bottom">
     <Panel title="My applications" meta="Current application status"><DataTable cols={['company','role','status']} rows={apps}/></Panel>
     <Panel title="Placement history" meta="Selected opportunities"><DataTable cols={['company','role','packageLpa','selectedAt']} rows={history}/></Panel>
   </div>
 </Page>
}

function FacultyDashboard(){
 const[data,setData]=useState(null),[busy,setBusy]=useState(true);
 useEffect(()=>{api.get('/faculty/overview').then(r=>setData(r.data)).catch(()=>setData(null)).finally(()=>setBusy(false))},[]);
 if(busy)return <Page eyebrow="FACULTY DASHBOARD" title="Loading…"><Loading/></Page>;
 return <Page eyebrow="FACULTY DASHBOARD" title={`Good morning, ${data?.faculty?.split(' ')[0]||'Faculty'}.`} subtitle={`${data?.department||'Department'} · ${data?.departmentCode||''}`}>
   <div className="stat-grid">
     <Stat label="STUDENTS" value={data?.students??'—'} note="In your department" icon={Users}/>
     <Stat label="SUBJECTS" value={data?.subjects??'—'} note="Available to manage" accent="orange" icon={BookOpen}/>
     <Stat label="ASSIGNMENTS" value={data?.assignments??'—'} note="Published by you" accent="cream" icon={FileText}/>
     <Stat label="PENDING REVIEW" value={data?.pendingReviews??'—'} note="Awaiting grades" icon={CheckCircle2}/>
   </div>
   <Panel title="Quick actions" meta="Common faculty workflows"><div className="action-list">
     <NavLink to="/faculty/attendance"><ClipboardCheck size={16}/>Take attendance<ArrowUpRight size={15}/></NavLink>
     <NavLink to="/faculty/marks"><GraduationCap size={16}/>Record marks<ArrowUpRight size={15}/></NavLink>
     <NavLink to="/faculty/assignments"><FileText size={16}/>Publish assignment<ArrowUpRight size={15}/></NavLink>
     <NavLink to="/faculty/submissions"><CheckCircle2 size={16}/>Review submissions<ArrowUpRight size={15}/></NavLink>
   </div></Panel>
 </Page>
}
function FacultyAttendance(){
 const[students,setStudents]=useState([]),[subjects,setSubjects]=useState([]),[form,setForm]=useState({studentId:'',subjectId:'',date:new Date().toISOString().slice(0,10),present:true}),[busy,setBusy]=useState(false),[message,setMessage]=useState(null);
 useEffect(()=>{Promise.all([api.get('/faculty/students'),api.get('/faculty/subjects')]).then(([a,b])=>{setStudents(a.data);setSubjects(b.data)}).catch(()=>{})},[]);
 async function save(e){e.preventDefault();setBusy(true);setMessage(null);try{await api.post('/faculty/attendance',{...form,studentId:Number(form.studentId),subjectId:Number(form.subjectId)});setMessage({type:'success',text:'Attendance saved successfully.'})}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to save attendance.'})}finally{setBusy(false)}}
 return <Page title="Mark attendance" subtitle="Select a student and subject instead of entering database IDs."><Panel title="Attendance entry"><form className="form-grid" onSubmit={save}>
   <label>Student<select required value={form.studentId} onChange={e=>setForm({...form,studentId:e.target.value})}><option value="">Choose student</option>{students.map(s=><option key={s.id} value={s.id}>{s.rollNumber} · {s.name}</option>)}</select></label>
   <label>Subject<select required value={form.subjectId} onChange={e=>setForm({...form,subjectId:e.target.value})}><option value="">Choose subject</option>{subjects.map(s=><option key={s.id} value={s.id}>{s.code} · {s.name}</option>)}</select></label>
   <label>Date<input type="date" value={form.date} onChange={e=>setForm({...form,date:e.target.value})}/></label>
   <label>Status<select value={form.present} onChange={e=>setForm({...form,present:e.target.value==='true'})}><option value="true">Present</option><option value="false">Absent</option></select></label>
   <button className="primary" disabled={busy}>{busy?'Saving…':'Save attendance'}</button>{message&&<FormMessage type={message.type} message={message.text}/>} 
 </form></Panel></Page>
}
function FacultyMarks(){
 const[students,setStudents]=useState([]),[subjects,setSubjects]=useState([]),[form,setForm]=useState({studentId:'',subjectId:'',internalMarks:'',totalMarks:50}),[busy,setBusy]=useState(false),[message,setMessage]=useState(null);
 useEffect(()=>{Promise.all([api.get('/faculty/students'),api.get('/faculty/subjects')]).then(([a,b])=>{setStudents(a.data);setSubjects(b.data)}).catch(()=>{})},[]);
 async function save(e){e.preventDefault();setMessage(null);if(Number(form.internalMarks)>Number(form.totalMarks))return setMessage({type:'error',text:'Marks obtained cannot exceed total marks.'});setBusy(true);try{await api.post('/faculty/marks',{...form,studentId:Number(form.studentId),subjectId:Number(form.subjectId),internalMarks:Number(form.internalMarks),totalMarks:Number(form.totalMarks)});setMessage({type:'success',text:'Marks saved successfully.'});setForm({...form,internalMarks:''})}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to save marks.'})}finally{setBusy(false)}}
 return <Page title="Record marks" subtitle="Choose the student and subject, then enter the assessment score."><Panel title="Marks entry"><form className="form-grid" onSubmit={save}>
   <label>Student<select required value={form.studentId} onChange={e=>setForm({...form,studentId:e.target.value})}><option value="">Choose student</option>{students.map(s=><option key={s.id} value={s.id}>{s.rollNumber} · {s.name}</option>)}</select></label>
   <label>Subject<select required value={form.subjectId} onChange={e=>setForm({...form,subjectId:e.target.value})}><option value="">Choose subject</option>{subjects.map(s=><option key={s.id} value={s.id}>{s.code} · {s.name}</option>)}</select></label>
   <label>Marks obtained<input required type="number" min="0" value={form.internalMarks} onChange={e=>setForm({...form,internalMarks:e.target.value})}/></label>
   <label>Total marks<input required type="number" min="1" value={form.totalMarks} onChange={e=>setForm({...form,totalMarks:e.target.value})}/></label>
   <button className="primary" disabled={busy}>{busy?'Saving…':'Save marks'}</button>{message&&<FormMessage type={message.type} message={message.text}/>} 
 </form></Panel></Page>
}
function FacultyAssignments(){
 const[subjects,setSubjects]=useState([]),[rows,setRows]=useState([]),[form,setForm]=useState({title:'',description:'',subjectId:'',deadline:''}),[busy,setBusy]=useState(false),[loadingSubjects,setLoadingSubjects]=useState(true),[subjectsError,setSubjectsError]=useState(false),[message,setMessage]=useState(null);
 async function loadAssignments(){try{const r=await api.get('/faculty/assignments/mine');setRows(Array.isArray(r.data)?r.data:[])}catch{setRows([])}}
 async function loadSubjects(){setLoadingSubjects(true);setSubjectsError(false);try{const r=await api.get('/faculty/subjects');setSubjects(Array.isArray(r.data)?r.data:[])}catch{setSubjects([]);setSubjectsError(true)}finally{setLoadingSubjects(false)}}
 useEffect(()=>{loadSubjects();loadAssignments()},[]);
 async function save(e){e.preventDefault();setMessage(null);if(!form.subjectId){setMessage({type:'error',text:'Please choose a subject before publishing.'});return}setBusy(true);try{await api.post('/faculty/assignments',{...form,subjectId:Number(form.subjectId)});setMessage({type:'success',text:'Assignment published successfully.'});setForm({title:'',description:'',subjectId:'',deadline:''});await loadAssignments()}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to publish assignment.'})}finally{setBusy(false)}}
 return <Page title="Assignments" subtitle="Publish tasks and see what you have already assigned.">
   <Panel title="New assignment" meta="Clear instructions + a firm deadline"><form onSubmit={save}>
    <label>Title<input required value={form.title} onChange={e=>setForm({...form,title:e.target.value})}/></label>
    <label>Description<textarea required value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label>
    <label>Subject<select required disabled={loadingSubjects||subjectsError||!subjects.length} value={form.subjectId} onChange={e=>setForm({...form,subjectId:e.target.value})}><option value="">{loadingSubjects?'Loading subjects…':subjectsError?'Unable to load subjects':subjects.length?'Choose subject':'No subjects available'}</option>{subjects.map(s=><option key={s.id} value={s.id}>{s.code} · {s.name}</option>)}</select></label>
    {subjectsError&&<FormMessage type="error" message="Could not load your department subjects. Refresh and try again."/>}
    {!loadingSubjects&&!subjectsError&&!subjects.length&&<FormMessage type="error" message="No subjects are assigned to this faculty department yet."/>}
    <label>Deadline<input required type="date" value={form.deadline} onChange={e=>setForm({...form,deadline:e.target.value})}/></label>
    <button className="primary" disabled={busy||loadingSubjects||subjectsError||!subjects.length}>{busy?'Publishing…':'Publish assignment'}</button>{message&&<FormMessage type={message.type} message={message.text}/>} 
   </form></Panel>
   <Panel title="Published by me" meta="Your current assignments"><DataTable cols={['title','subject','deadline','description']} rows={rows}/></Panel>
 </Page>
}

function FacultySubmissions(){const[rows,setRows]=useState([]);const[busy,setBusy]=useState(null);const[message,setMessage]=useState(null);const[gradeForm,setGradeForm]=useState(null);async function load(){const r=await api.get('/faculty/submissions');setRows(r.data)}useEffect(()=>{load()},[]);async function grade(id){const marks=Number(gradeForm?.marks);if(!Number.isFinite(marks)||marks<0||marks>100)return setMessage({type:'error',text:'Marks must be between 0 and 100.'});const feedback=String(gradeForm?.feedback||'').trim();setBusy(id);setMessage(null);try{await api.patch(`/faculty/submissions/${id}/grade`,{marks,feedback});setGradeForm(null);await load();setMessage({type:'success',text:'Submission graded successfully.'})}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to grade submission'})}finally{setBusy(null)}}return <Page title="Review submissions" subtitle="Grade student work and leave concise feedback."><div className="assignment-list">{message&&<FormMessage type={message.type} message={message.text}/>} {rows.map(s=><article className="assignment-item" key={s.id}><div><span className="label">{s.assignment}</span><h3>{s.student}</h3><p>{s.fileName}</p><small>Submitted · {s.submittedAt}</small></div><div className="assignment-action">{gradeForm?.id===s.id?<div className="grade-form"><input type="number" min="0" max="100" step="0.01" placeholder="Marks 0–100" value={gradeForm.marks} onChange={e=>setGradeForm({...gradeForm,marks:e.target.value})}/><input placeholder="Feedback (optional)" value={gradeForm.feedback} onChange={e=>setGradeForm({...gradeForm,feedback:e.target.value})}/><button className="primary" disabled={busy===s.id} onClick={()=>grade(s.id)}>{busy===s.id?'Saving…':'Save grade'}</button><button className="secondary-btn" type="button" onClick={()=>setGradeForm(null)}>Cancel</button></div>:<><span className="status-ok">{s.marks==='—'?'Not graded':`Marks: ${s.marks}`}</span><button className="primary" onClick={()=>setGradeForm({id:s.id,marks:s.marks==='—'?'':s.marks,feedback:''})}>{s.marks==='—'?'Grade':'Update'}</button></>}</div></article>)}{!rows.length&&<div className="panel empty">No submissions yet.</div>}</div></Page>}
function AdminDashboard(){
 const[stats,setStats]=useState(null);
 useEffect(()=>{api.get('/admin/overview').then(r=>setStats(r.data)).catch(()=>setStats(null))},[]);
 return <Page eyebrow="ADMIN DASHBOARD" title="System overview" subtitle="A live snapshot of users, academics and placement operations.">
  <div className="stat-grid">
   <Stat label="STUDENTS" value={stats?.students??'—'} note="Registered students" icon={Users}/>
   <Stat label="FACULTY" value={stats?.faculty??'—'} note="Faculty accounts" accent="orange" icon={GraduationCap}/>
   <Stat label="COMPANIES" value={stats?.companies??'—'} note="Placement partners" accent="cream" icon={Building2}/>
   <Stat label="APPLICATIONS" value={stats?.applications??'—'} note={stats?(stats.selected+' selected'):'Placement activity'} icon={BriefcaseBusiness}/>
  </div>
  <div className="cards admin-cards">
   <article className="op-card"><span className="label">USER DIRECTORY</span><h3>{stats?.activeUsers??'—'} active accounts</h3><p>Review student, faculty and admin access.</p><NavLink className="primary" to="/admin/users">Manage users <ArrowUpRight size={14}/></NavLink></article>
   <article className="op-card"><span className="label">PLACEMENT</span><h3>{stats?.drives??'—'} placement drives</h3><p>Companies and opportunities are managed centrally.</p><NavLink className="primary" to="/admin/drives">Manage drives <ArrowUpRight size={14}/></NavLink></article>
   <article className="op-card"><span className="label">SYSTEM</span><h3>{stats?.users??'—'} total users</h3><p>Core API and database services are connected.</p><div className="status-ok"><CheckCircle2 size={15}/> Operational</div></article>
  </div>
 </Page>
}
function AdminCompanies(){
 const[form,setForm]=useState({name:'',website:'',description:''}),[rows,setRows]=useState([]),[busy,setBusy]=useState(false),[message,setMessage]=useState(null);
 async function load(){const r=await api.get('/admin/companies');setRows(r.data)}
 useEffect(()=>{load()},[]);
 async function save(e){e.preventDefault();setMessage(null);setBusy(true);try{await api.post('/admin/companies',form);setForm({name:'',website:'',description:''});setMessage({type:'success',text:'Company added successfully.'});await load()}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to create company.'})}finally{setBusy(false)}}
 return <Page title="Companies" subtitle="Manage organizations available for placement drives.">
  <div className="profile-grid"><Panel title="New company"><form onSubmit={save}><label>Company name<input required value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></label><label>Website<input value={form.website} onChange={e=>setForm({...form,website:e.target.value})}/></label><label>Description<textarea value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Saving…':'Add company'}</button>{message&&<FormMessage type={message.type} message={message.text}/>} </form></Panel><Panel title="Company directory" meta="Registered placement partners"><DataTable cols={['name','website','description']} rows={rows}/></Panel></div>
 </Page>
}
function AdminDrives(){
 const[form,setForm]=useState({companyId:'',jobRole:'',packageLpa:'',minimumCgpa:'',deadline:'',eligibleBranches:'CS,IT'}),[companies,setCompanies]=useState([]),[rows,setRows]=useState([]),[busy,setBusy]=useState(false),[message,setMessage]=useState(null);
 async function load(){const[a,b]=await Promise.all([api.get('/admin/companies'),api.get('/admin/drives')]);setCompanies(a.data);setRows(b.data)}
 useEffect(()=>{load()},[]);
 async function save(e){e.preventDefault();setMessage(null);setBusy(true);try{await api.post('/admin/drives',form);setForm({...form,jobRole:'',packageLpa:'',minimumCgpa:'',deadline:''});setMessage({type:'success',text:'Placement drive created successfully.'});await load()}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to create drive.'})}finally{setBusy(false)}}
 return <Page title="Placement drives" subtitle="Create opportunities and review the current placement pipeline.">
  <Panel title="New placement drive"><form className="form-grid" onSubmit={save}><label>Company<select required value={form.companyId} onChange={e=>setForm({...form,companyId:e.target.value})}><option value="">Select company</option>{companies.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></label><label>Job role<input required value={form.jobRole} onChange={e=>setForm({...form,jobRole:e.target.value})}/></label><label>Package (LPA)<input required type="number" step="0.1" value={form.packageLpa} onChange={e=>setForm({...form,packageLpa:e.target.value})}/></label><label>Minimum CGPA<input required type="number" step="0.1" value={form.minimumCgpa} onChange={e=>setForm({...form,minimumCgpa:e.target.value})}/></label><label>Deadline<input required type="date" value={form.deadline} onChange={e=>setForm({...form,deadline:e.target.value})}/></label><label>Eligible branches<input value={form.eligibleBranches} onChange={e=>setForm({...form,eligibleBranches:e.target.value})}/></label><button className="primary" disabled={busy}>{busy?'Creating…':'Create drive'}</button>{message&&<FormMessage type={message.type} message={message.text}/>} </form></Panel>  <Panel title="Drive directory" meta="Published placement opportunities"><DataTable cols={['company','jobRole','packageLpa','minimumCgpa','deadline','eligibleBranches']} rows={rows.map(d=>({...d,company:d.company?.name||'—'}))}/></Panel>
 </Page>
}
function AdminUsers(){
 const[rows,setRows]=useState([]),[busy,setBusy]=useState(null),[message,setMessage]=useState(null);
 async function load(){const r=await api.get('/admin/users');setRows(r.data)}
 useEffect(()=>{load()},[]);
 async function toggle(id,active){setBusy(id);try{await api.patch('/admin/users/'+id+'/active',{active:!active});await load()}catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to update user'})}finally{setBusy(null)}}
 return <Page title="User management" subtitle="Review account roles and control access to the portal.">
  <Panel title="User directory" meta="Account status and role overview">
   {message&&<FormMessage type={message.type} message={message.text}/>}<div className="table-wrap"><table><thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Access</th></tr></thead><tbody>{rows.map(u=><tr key={u.id}><td><b>{u.name}</b></td><td>{u.email}</td><td><span className="label">{u.role}</span></td><td><span className={u.active?'status-ok':'status-bad'}>{u.active?'Active':'Inactive'}</span></td><td><button className={u.active?'secondary-btn':'primary'} disabled={busy===u.id} onClick={()=>toggle(u.id,u.active)}>{busy===u.id?'Updating…':u.active?'Deactivate':'Activate'}</button></td></tr>)}</tbody></table></div>
  </Panel>
 </Page>
}

function AdminApplications(){
 const[rows,setRows]=useState([]),[busy,setBusy]=useState(null),[message,setMessage]=useState(null);
 async function load(){const r=await api.get('/admin/applications');setRows(r.data)}
 useEffect(()=>{load()},[]);
 async function changeStatus(id,status){
   setBusy(id);
   try{await api.patch(`/placement-upgrades/admin/applications/${id}/status`,{status});await load()}
   catch(e){setMessage({type:'error',text:e.response?.data?.message||'Unable to update status'})}
   finally{setBusy(null)}
 }
 return <Page title="Applications" subtitle="Review and update placement application progress.">
   {message&&<FormMessage type={message.type} message={message.text}/>}<div className="table-wrap"><table><thead><tr><th>Student</th><th>Company</th><th>Role</th><th>Applied</th><th>Status</th></tr></thead><tbody>
   {rows.map(r=><tr key={r.id}><td>{r.student?.user?.fullName||r.student?.name||'Student'}</td><td>{r.placementDrive?.company?.name||'—'}</td><td>{r.placementDrive?.jobRole||'—'}</td><td>{r.appliedAt||'—'}</td><td><select className="status-select" disabled={busy===r.id} value={r.status} onChange={e=>changeStatus(r.id,e.target.value)}>{['APPLIED','SHORTLISTED','INTERVIEW','SELECTED','REJECTED'].map(s=><option key={s}>{s}</option>)}</select></td></tr>)}
   </tbody></table></div>

 </Page>
}

function Timetable(){
 const[rows,setRows]=useState([]),[day,setDay]=useState('ALL');
 useEffect(()=>{api.get('/student/timetable').then(r=>setRows(r.data)).catch(()=>setRows([]))},[]);
 const days=['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
 const visible=day==='ALL'?rows:rows.filter(x=>x.day===day);
 const emptyTitle=day==='ALL'?'No timetable entries yet':`No classes on ${day[0]+day.slice(1).toLowerCase()}`;
 return <Page title="My timetable" subtitle="Your weekly class schedule, synced from the campus portal." action={<div className="semester">Semester 5 <i>•</i> Weekly view</div>}>
   <div className="day-tabs"><button className={day==='ALL'?'active':''} onClick={()=>setDay('ALL')}>All</button>{days.map(d=><button key={d} className={day===d?'active':''} onClick={()=>setDay(d)}>{d.slice(0,3)}</button>)}</div>
   {!visible.length?<EmptyState icon={CalendarDays} title={emptyTitle} message="Your class schedule will appear here once it is published."/>:<div className="timetable-grid">{visible.map(x=><article className="class-card" key={x.id}>
      <div className="class-time"><Clock3 size={15}/><b>{x.startTime.slice(0,5)} – {x.endTime.slice(0,5)}</b><span>{x.day}</span></div>
      <div className="class-main"><span className="label">{x.code}</span><h3>{x.subject}</h3><p>{x.faculty}</p></div>
      <div className="class-room"><MapPin size={14}/>{x.room}</div>
   </article>)}</div>}
 </Page>
}

function Subjects(){
 const[rows,setRows]=useState([]);
 useEffect(()=>{api.get('/student/subjects').then(r=>setRows(r.data)).catch(()=>setRows([]))},[]);
 return <Page title="My subjects" subtitle="Academic snapshot for your current semester.">
   <div className="subject-cards">{rows.map(s=><article className="subject-card" key={s.id}><span className="label">{s.code}</span><h3>{s.name}</h3><div className="subject-meta"><span>Credits <b>{s.credits}</b></span><span>Attendance <b>{s.attendance}%</b></span><span>Marks <b>{s.internalMarks}/{s.totalMarks||'—'}</b></span></div></article>)}{!rows.length&&<div className="panel empty">No subjects available.</div>}</div>
 </Page>
}

function EmptyState({icon:Icon=Inbox,title="Nothing here yet",message="There are no records to show right now."}){return <div className="empty-state"><span className="empty-icon"><Icon size={18}/></span><div><b>{title}</b><p>{message}</p></div></div>}

function FormMessage({type="success",message}){if(!message)return null;return <div className={`form-message ${type}`}><span>{type==="success"?<CheckCircle2 size={15}/>:<CircleAlert size={15}/>}</span>{message}</div>}

function DataTable({cols,rows,emptyTitle="Nothing here yet",emptyMessage="There are no records to show right now."}){if(!rows?.length)return <EmptyState title={emptyTitle} message={emptyMessage}/>;return <div className="table-wrap"><table><thead><tr>{cols.map(c=><th key={c}>{c.replace(/([A-Z])/g,' $1')}</th>)}</tr></thead><tbody>{rows.map((r,i)=><tr key={r.id||i}>{cols.map(c=><td key={c}>{typeof r[c]==='object'?JSON.stringify(r[c]):String(r[c]??'—')}</td>)}</tr>)}</tbody></table></div>}

export default function App(){
  const r=role();
  const protectedPage = (element) => <Protected><Shell>{element}</Shell></Protected>;
  return <Routes>
    <Route path="/login" element={r ? <Navigate to="/" replace/> : <Login/>}/>

    {r === 'STUDENT' && <>
      <Route path="/" element={protectedPage(<StudentDashboard/>)}/>
      <Route path="/profile" element={protectedPage(<Profile/>)}/><Route path="/timetable" element={protectedPage(<Timetable/>)}/><Route path="/subjects" element={protectedPage(<Subjects/>)}/><Route path="/performance" element={protectedPage(<Performance/>)}/>
      <Route path="/attendance" element={protectedPage(<Attendance/>)}/>
      <Route path="/marks" element={protectedPage(<Marks/>)}/>
      <Route path="/assignments" element={protectedPage(<Assignments/>)}/>
      <Route path="/placements" element={protectedPage(<Placements/>)}/>
    </>}

    {r === 'FACULTY' && <>
      <Route path="/" element={protectedPage(<FacultyDashboard/>)}/>
      <Route path="/faculty/attendance" element={protectedPage(<FacultyAttendance/>)}/>
      <Route path="/faculty/marks" element={protectedPage(<FacultyMarks/>)}/>
      <Route path="/faculty/assignments" element={protectedPage(<FacultyAssignments/>)}/><Route path="/faculty/submissions" element={protectedPage(<FacultySubmissions/>)}/>
    </>}

    {r === 'ADMIN' && <>
      <Route path="/" element={protectedPage(<AdminDashboard/>)}/>
      <Route path="/admin/users" element={protectedPage(<AdminUsers/>)}/><Route path="/admin/companies" element={protectedPage(<AdminCompanies/>)}/>
      <Route path="/admin/drives" element={protectedPage(<AdminDrives/>)}/>
      <Route path="/admin/applications" element={protectedPage(<AdminApplications/>)}/>
    </>}

    <Route path="*" element={<Navigate to={r ? "/" : "/login"} replace/>}/>
  </Routes>
}