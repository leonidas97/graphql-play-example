const { useState } = React

const App = () => {
  const [nums, setNums] = useState([1,2,3,4])

  return (
    <div>
      <Welcome />
      <About />
      {nums.map(n => <p key={n}>{n}</p>)}
    </div>
  )
}
